package net.ukr.kondrashev.controllers;

import net.ukr.kondrashev.entities.Category;
import net.ukr.kondrashev.entities.CustomUser;
import net.ukr.kondrashev.repositories.CategoryRepository;
import net.ukr.kondrashev.entities.Word;
import net.ukr.kondrashev.repositories.CustomUserRepository;
import net.ukr.kondrashev.repositories.WordRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class WordController {
    @Autowired
    private CustomUserRepository customUserRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WordRepository wordRepository;

    @GetMapping("get/words")
    public List<Word> doGetWords(@RequestParam(required = false, defaultValue = "0") Integer page,
                                 @RequestParam Long categoryId) {
        Category category = categoryRepository.getOne(categoryId);
        return wordRepository.findByCategory(category, PageRequest.of(page, 5));
    }

    @PostMapping("add/word")
    public boolean addWord(@RequestParam Long userId, @RequestParam Long categoryId, @RequestBody Word word) throws Exception {
        CustomUser customUser = customUserRepository.getOne(userId);
        Category category = categoryRepository.getOne(categoryId);
        if (wordRepository.existsByName(word.getName(), customUser.getLogin()) == false) {
            wordRepository.save(new Word(category, word.getName(), word.getMeaning(), customUser.getLogin()));
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/main/json/users.json"));
            List<JSONObject> users = jsonArray;
            for (JSONObject user : users) {
                if (customUser.getLogin().equals((String) user.get("login"))) {
                    List<JSONObject> categories = (JSONArray) user.get("categories");
                    for (JSONObject currentCategory : categories) {
                        if (category.getName().equals((String) currentCategory.get("name"))) {
                            JSONArray words = (JSONArray) currentCategory.get("words");
                            Word wordCurrent = wordRepository.findByName(word.getName());
                            JSONObject currentWord = new JSONObject();
                            currentWord.put("id", wordCurrent.getId());
                            currentWord.put("name", wordCurrent.getName());
                            currentWord.put("meaning", wordCurrent.getMeaning());
                            currentWord.put("userName", wordCurrent.getUserName());
                            words.add(currentWord);
                            FileWriter file = new FileWriter("./src/main/json/users.json");
                            file.write(jsonArray.toJSONString());
                            file.flush();
                            file.close();
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @PostMapping("delete/words")
    public void deleteWords(@RequestParam Long userId, @RequestParam long categoryId, @RequestBody String wordListId) throws Exception {
        ArrayList<Long> IdListWord = new ArrayList<>();
        for (String box : wordListId.substring(1, wordListId.length() - 1).split(",")) {
            IdListWord.add(Long.parseLong(box));
        }
        ArrayList<String> nameListWord = new ArrayList<>();
        for (long wordId : IdListWord) {
            Word wordCurrent = wordRepository.getOne(wordId);
            nameListWord.add(wordCurrent.getName());
            wordRepository.deleteById(wordId);
        }
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/main/json/users.json"));
        List<JSONObject> users = jsonArray;
        CustomUser customUser = customUserRepository.getOne(userId);
        Category category = categoryRepository.getOne(categoryId);
        for (JSONObject user : users) {
            if (((String) user.get("login")).equals(customUser.getLogin())) {
                List<JSONObject> categories = (JSONArray) user.get("categories");
                List<JSONObject> listWordsDelete = new ArrayList<>();
                for (JSONObject currentCategory : categories) {
                    if (((String) currentCategory.get("name")).equals(category.getName())) {
                        List<JSONObject> words = (JSONArray) currentCategory.get("words");
                        for (String currentNameWord : nameListWord) {
                            for (JSONObject word : words) {
                                if (((String) word.get("name")).equals(currentNameWord)) {
                                    listWordsDelete.add(word);
                                }
                            }
                        }
                        for (JSONObject word : listWordsDelete) {
                            words.remove(word);
                            FileWriter file = new FileWriter("./src/main/json/users.json");
                            file.write(jsonArray.toJSONString());
                            file.flush();
                            file.close();
                        }
                    }
                }
            }
        }
    }
}
