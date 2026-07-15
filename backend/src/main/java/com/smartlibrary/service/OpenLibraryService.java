package com.smartlibrary.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlibrary.entity.Author;
import com.smartlibrary.entity.Category;
import com.smartlibrary.entity.Publisher;
import com.smartlibrary.repository.AuthorRepository;
import com.smartlibrary.repository.CategoryRepository;
import com.smartlibrary.repository.PublisherRepository;
import com.smartlibrary.exception.LibraryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import com.smartlibrary.dto.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenLibraryService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> fetchBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new LibraryException("ISBN is required", HttpStatus.BAD_REQUEST);
        }
        
        String cleanIsbn = isbn.trim().replace("-", "").replace(" ", "");
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + cleanIsbn + "&jscmd=data&format=json";
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            String bibKey = "ISBN:" + cleanIsbn;
            
            if (root == null || !root.has(bibKey)) {
                throw new LibraryException("Book not found.", HttpStatus.NOT_FOUND);
            }
            
            JsonNode bookNode = root.get(bibKey);
            Map<String, Object> details = new HashMap<>();
            
            details.put("isbn", cleanIsbn);
            details.put("title", bookNode.has("title") ? bookNode.get("title").asText() : "");
            details.put("subtitle", bookNode.has("subtitle") ? bookNode.get("subtitle").asText() : "");
            
            // Resolve Author
            String firstAuthorName = "Unknown Author";
            if (bookNode.has("authors") && bookNode.get("authors").size() > 0) {
                firstAuthorName = bookNode.get("authors").get(0).get("name").asText();
            }
            String finalAuthorName = firstAuthorName;
            Author authorObj = authorRepository.findByName(firstAuthorName)
                    .orElseGet(() -> authorRepository.save(Author.builder().name(finalAuthorName).build()));
            details.put("authorId", authorObj.getId());
            details.put("authorName", authorObj.getName());
            
            // Resolve Publisher
            String firstPublisherName = "Unknown Publisher";
            if (bookNode.has("publishers") && bookNode.get("publishers").size() > 0) {
                firstPublisherName = bookNode.get("publishers").get(0).get("name").asText();
            }
            String finalPubName = firstPublisherName;
            Publisher pubObj = publisherRepository.findByName(firstPublisherName)
                    .orElseGet(() -> publisherRepository.save(Publisher.builder().name(finalPubName).build()));
            details.put("publisherId", pubObj.getId());
            details.put("publisherName", pubObj.getName());
            
            // Resolve Category
            String categoryName = "General";
            if (bookNode.has("subjects") && bookNode.get("subjects").size() > 0) {
                categoryName = bookNode.get("subjects").get(0).get("name").asText();
                if (categoryName.length() > 50) {
                    categoryName = categoryName.substring(0, 50);
                }
            }
            String finalCatName = categoryName;
            Category catObj = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> categoryRepository.save(Category.builder().name(finalCatName).description("Auto-created from ISBN lookup").build()));
            details.put("categoryId", catObj.getId());
            details.put("categoryName", catObj.getName());
            
            // Publication Year
            Integer publishYear = null;
            if (bookNode.has("publish_date")) {
                String publishDateStr = bookNode.get("publish_date").asText();
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\b\\d{4}\\b").matcher(publishDateStr);
                if (matcher.find()) {
                    publishYear = Integer.parseInt(matcher.group());
                }
            }
            details.put("publicationYear", publishYear);
            
            // Number of Pages
            Integer pages = null;
            if (bookNode.has("number_of_pages")) {
                pages = bookNode.get("number_of_pages").asInt();
            }
            details.put("numberOfPages", pages);
            details.put("pageCount", pages);
            
            // Cover Image URL
            String coverUrl = "";
            if (bookNode.has("cover")) {
                JsonNode coverNode = bookNode.get("cover");
                if (coverNode.has("large")) {
                    coverUrl = coverNode.get("large").asText();
                } else if (coverNode.has("medium")) {
                    coverUrl = coverNode.get("medium").asText();
                } else if (coverNode.has("small")) {
                    coverUrl = coverNode.get("small").asText();
                }
            }
            details.put("coverImageUrl", coverUrl);
            details.put("coverImage", coverUrl);
            
            // Language
            String language = "English";
            if (bookNode.has("languages")) {
                for (JsonNode lang : bookNode.get("languages")) {
                    if (lang.has("name")) {
                        language = lang.get("name").asText();
                        break;
                    }
                }
            }
            details.put("language", language);
            
            // Description / Excerpt
            String description = "";
            if (bookNode.has("description")) {
                description = bookNode.get("description").asText();
            } else if (bookNode.has("excerpts") && bookNode.get("excerpts").size() > 0) {
                description = bookNode.get("excerpts").get(0).has("text") ? bookNode.get("excerpts").get(0).get("text").asText() : "";
            }
            details.put("description", description);
            
            return details;
            
        } catch (LibraryException e) {
            throw e;
        } catch (Exception e) {
            throw new LibraryException("Error communicating with Open Library API: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "olSearchCache", key = "#query + '-' + #type")
    public List<OpenLibraryBookDto> searchBooks(String query, String type) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String cleanQuery = query.trim();
        String url;
        if ("title".equalsIgnoreCase(type)) {
            url = "https://openlibrary.org/search.json?title=" + cleanQuery;
        } else if ("author".equalsIgnoreCase(type)) {
            url = "https://openlibrary.org/search.json?author=" + cleanQuery;
        } else if ("isbn".equalsIgnoreCase(type)) {
            url = "https://openlibrary.org/search.json?isbn=" + cleanQuery;
        } else if ("subject".equalsIgnoreCase(type)) {
            url = "https://openlibrary.org/search.json?subject=" + cleanQuery;
        } else {
            url = "https://openlibrary.org/search.json?q=" + cleanQuery;
        }
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode docs = root.get("docs");
            List<OpenLibraryBookDto> list = new ArrayList<>();
            if (docs != null && docs.isArray()) {
                int limit = Math.min(docs.size(), 20);
                for (int i = 0; i < limit; i++) {
                    JsonNode doc = docs.get(i);
                    String key = doc.has("key") ? doc.get("key").asText().replace("/works/", "") : "";
                    String title = doc.has("title") ? doc.get("title").asText() : "Untitled";
                    
                    List<String> authors = new ArrayList<>();
                    if (doc.has("author_name") && doc.get("author_name").isArray()) {
                        for (JsonNode aut : doc.get("author_name")) {
                            authors.add(aut.asText());
                        }
                    }
                    
                    String coverUrl = "";
                    if (doc.has("cover_i")) {
                        coverUrl = "https://covers.openlibrary.org/b/id/" + doc.get("cover_i").asLong() + "-M.jpg";
                    } else if (doc.has("isbn") && doc.get("isbn").isArray() && doc.get("isbn").size() > 0) {
                        coverUrl = "https://covers.openlibrary.org/b/isbn/" + doc.get("isbn").get(0).asText() + "-M.jpg";
                    }
                    
                    Integer firstPublishYear = doc.has("first_publish_year") ? doc.get("first_publish_year").asInt() : null;
                    
                    List<String> publishers = new ArrayList<>();
                    if (doc.has("publisher") && doc.get("publisher").isArray()) {
                        for (JsonNode pub : doc.get("publisher")) {
                            publishers.add(pub.asText());
                        }
                    }
                    
                    String isbn = "";
                    if (doc.has("isbn") && doc.get("isbn").isArray() && doc.get("isbn").size() > 0) {
                        isbn = doc.get("isbn").get(0).asText();
                    }
                    
                    list.add(OpenLibraryBookDto.builder()
                            .key(key)
                            .title(title)
                            .authors(authors)
                            .coverUrl(coverUrl)
                            .firstPublishYear(firstPublishYear)
                            .publishers(publishers)
                            .isbn(isbn)
                            .build());
                }
            }
            return list;
        } catch (Exception e) {
            throw new LibraryException("Search failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "olWorkCache", key = "#workId")
    public OpenLibraryWorkDetailDto getWorkDetail(String workId) {
        if (workId == null || workId.trim().isEmpty()) {
            throw new LibraryException("Work ID is required", HttpStatus.BAD_REQUEST);
        }
        String cleanId = workId.trim().replace("/works/", "");
        String url = "https://openlibrary.org/works/" + cleanId + ".json";
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            String title = root.has("title") ? root.get("title").asText() : "Untitled";
            
            String description = "";
            if (root.has("description")) {
                JsonNode descNode = root.get("description");
                if (descNode.isObject()) {
                    description = descNode.has("value") ? descNode.get("value").asText() : "";
                } else {
                    description = descNode.asText();
                }
            }
            
            List<String> subjects = new ArrayList<>();
            if (root.has("subjects") && root.get("subjects").isArray()) {
                for (JsonNode sub : root.get("subjects")) {
                    subjects.add(sub.asText());
                }
            }
            
            String firstPublishDate = root.has("first_publish_date") ? root.get("first_publish_date").asText() : "";
            
            String coverUrl = "";
            if (root.has("covers") && root.get("covers").isArray() && root.get("covers").size() > 0) {
                coverUrl = "https://covers.openlibrary.org/b/id/" + root.get("covers").get(0).asLong() + "-L.jpg";
            }
            
            List<String> authorKeys = new ArrayList<>();
            List<String> authorNames = new ArrayList<>();
            if (root.has("authors") && root.get("authors").isArray()) {
                for (JsonNode autNode : root.get("authors")) {
                    if (autNode.has("author")) {
                        String autKey = autNode.get("author").get("key").asText().replace("/authors/", "");
                        authorKeys.add(autKey);
                        try {
                            authorNames.add(fetchAuthorName(autKey));
                        } catch (Exception e) {
                            authorNames.add("Unknown Author");
                        }
                    }
                }
            }
            
            Integer editionsCount = 1;
            try {
                String edUrl = "https://openlibrary.org/works/" + cleanId + "/editions.json?limit=1";
                String edRes = restTemplate.getForObject(edUrl, String.class);
                JsonNode edRoot = objectMapper.readTree(edRes);
                if (edRoot.has("size")) {
                    editionsCount = edRoot.get("size").asInt();
                }
            } catch (Exception e) {
                // Ignore editions count query failure
            }

            String isbn = "";
            try {
                List<OpenLibraryEditionDto> editions = getEditions(cleanId);
                for (OpenLibraryEditionDto ed : editions) {
                    if (ed.getIsbn() != null && !ed.getIsbn().trim().isEmpty()) {
                        isbn = ed.getIsbn().trim();
                        break;
                    }
                }
            } catch (Exception e) {
                // Ignore editions ISBN fetch failure
            }

            return OpenLibraryWorkDetailDto.builder()
                    .key(cleanId)
                    .title(title)
                    .description(description)
                    .subjects(subjects)
                    .firstPublishDate(firstPublishDate)
                    .editionsCount(editionsCount)
                    .coverUrl(coverUrl)
                    .authorKeys(authorKeys)
                    .authors(authorNames)
                    .isbn(isbn)
                    .build();
        } catch (Exception e) {
            throw new LibraryException("Failed to fetch book details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "olAuthorCache", key = "#authorId")
    public OpenLibraryAuthorDto getAuthorDetail(String authorId) {
        if (authorId == null || authorId.trim().isEmpty()) {
            throw new LibraryException("Author ID is required", HttpStatus.BAD_REQUEST);
        }
        String cleanId = authorId.trim().replace("/authors/", "");
        String url = "https://openlibrary.org/authors/" + cleanId + ".json";
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            String name = root.has("name") ? root.get("name").asText() : "Unknown Author";
            
            String bio = "";
            if (root.has("bio")) {
                JsonNode bioNode = root.get("bio");
                if (bioNode.isObject()) {
                    bio = bioNode.has("value") ? bioNode.get("value").asText() : "";
                } else {
                    bio = bioNode.asText();
                }
            }
            
            String birthDate = root.has("birth_date") ? root.get("birth_date").asText() : "";
            String deathDate = root.has("death_date") ? root.get("death_date").asText() : "";
            
            String photoUrl = "";
            if (root.has("photos") && root.get("photos").isArray() && root.get("photos").size() > 0) {
                photoUrl = "https://covers.openlibrary.org/a/id/" + root.get("photos").get(0).asLong() + "-M.jpg";
            }
            
            return OpenLibraryAuthorDto.builder()
                    .key(cleanId)
                    .name(name)
                    .biography(bio)
                    .birthDate(birthDate)
                    .deathDate(deathDate)
                    .photoUrl(photoUrl)
                    .build();
        } catch (Exception e) {
            throw new LibraryException("Failed to fetch author details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "olSubjectCache", key = "#subjectName")
    public List<OpenLibraryBookDto> getBooksBySubject(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String cleanSubject = subjectName.trim().toLowerCase().replace(" ", "_");
        String url = "https://openlibrary.org/subjects/" + cleanSubject + ".json?limit=12";
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode works = root.get("works");
            List<OpenLibraryBookDto> list = new ArrayList<>();
            if (works != null && works.isArray()) {
                for (int i = 0; i < works.size(); i++) {
                    JsonNode work = works.get(i);
                    String key = work.has("key") ? work.get("key").asText().replace("/works/", "") : "";
                    String title = work.has("title") ? work.get("title").asText() : "Untitled";
                    
                    List<String> authors = new ArrayList<>();
                    if (work.has("authors") && work.get("authors").isArray()) {
                        for (JsonNode aut : work.get("authors")) {
                            if (aut.has("name")) {
                                authors.add(aut.get("name").asText());
                            }
                        }
                    }
                    
                    String coverUrl = "";
                    if (work.has("cover_id") && !work.get("cover_id").isNull()) {
                        coverUrl = "https://covers.openlibrary.org/b/id/" + work.get("cover_id").asLong() + "-M.jpg";
                    }
                    
                    Integer firstPublishYear = work.has("first_publish_year") ? work.get("first_publish_year").asInt() : null;
                    
                    List<String> publishers = new ArrayList<>();
                    
                    list.add(OpenLibraryBookDto.builder()
                            .key(key)
                            .title(title)
                            .authors(authors)
                            .coverUrl(coverUrl)
                            .firstPublishYear(firstPublishYear)
                            .publishers(publishers)
                            .build());
                }
            }
            return list;
        } catch (Exception e) {
            throw new LibraryException("Failed to fetch subject books: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "olWorkCache", key = "#workId + '-editions'")
    public List<OpenLibraryEditionDto> getEditions(String workId) {
        if (workId == null || workId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String cleanId = workId.trim().replace("/works/", "");
        String url = "https://openlibrary.org/works/" + cleanId + "/editions.json?limit=10";
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode entries = root.get("entries");
            List<OpenLibraryEditionDto> list = new ArrayList<>();
            if (entries != null && entries.isArray()) {
                for (int i = 0; i < entries.size(); i++) {
                    JsonNode entry = entries.get(i);
                    String title = entry.has("title") ? entry.get("title").asText() : "Untitled Edition";
                    String publishDate = entry.has("publish_date") ? entry.get("publish_date").asText() : "";
                    
                    List<String> publishers = new ArrayList<>();
                    if (entry.has("publishers") && entry.get("publishers").isArray()) {
                        for (JsonNode pub : entry.get("publishers")) {
                            publishers.add(pub.asText());
                        }
                    }
                    
                    String isbn = "";
                    if (entry.has("isbn_13") && entry.get("isbn_13").isArray() && entry.get("isbn_13").size() > 0) {
                        isbn = entry.get("isbn_13").get(0).asText();
                    } else if (entry.has("isbn_10") && entry.get("isbn_10").isArray() && entry.get("isbn_10").size() > 0) {
                        isbn = entry.get("isbn_10").get(0).asText();
                    }
                    
                    list.add(OpenLibraryEditionDto.builder()
                            .title(title)
                            .publishDate(publishDate)
                            .publishers(publishers)
                            .isbn(isbn)
                            .build());
                }
            }
            return list;
        } catch (Exception e) {
            throw new LibraryException("Failed to fetch book editions: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "olAuthorNameCache", key = "#authorId")
    public String fetchAuthorName(String authorId) {
        String cleanId = authorId.trim().replace("/authors/", "");
        String url = "https://openlibrary.org/authors/" + cleanId + ".json";
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            return root.has("name") ? root.get("name").asText() : "Unknown Author";
        } catch (Exception e) {
            return "Unknown Author";
        }
    }
}
