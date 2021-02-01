package net.ukr.kondrashev.controllers;

import net.ukr.kondrashev.entities.Category;
import net.ukr.kondrashev.repositories.CategoryRepository;
import net.ukr.kondrashev.entities.CustomUser;
import net.ukr.kondrashev.repositories.CustomUserRepository;
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
public class CategoryController {
    @Autowired
    private CustomUserRepository customUserRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("get/categories")
    public List<Category> doGetCategory(@RequestParam long userId, @RequestParam(required = false, defaultValue = "0") Integer page) {
        CustomUser customUser = customUserRepository.getOne(userId);
        return categoryRepository.findByUser(customUser, PageRequest.of(page, 5));
    }

    @PostMapping("add/category")
    public boolean addCategory(@RequestParam Long userId, @RequestBody Category category) throws Exception {
        CustomUser customUser = customUserRepository.getOne(userId);
        if (categoryRepository.existsByName(category.getName(), customUser.getLogin()) == false) {
            categoryRepository.save(new Category(customUser, category.getName(), customUser.getLogin()));
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/main/json/users.json"));
            List<JSONObject> users = jsonArray;
            for (JSONObject user : users) {
                if (customUser.getLogin().equals((String) user.get("login"))) {
                    JSONArray categories = (JSONArray) user.get("categories");
                    Category categoryCurrent = categoryRepository.findByName(category.getName());
                    JSONObject currentCategory = new JSONObject();
                    currentCategory.put("id", categoryCurrent.getId());
                    currentCategory.put("name", categoryCurrent.getName());
                    currentCategory.put("userName", categoryCurrent.getUserName());
                    List<JSONObject> listWords = new ArrayList<>();
                    currentCategory.put("words", listWords);
                    categories.add(currentCategory);
                    FileWriter file = new FileWriter("./src/main/json/users.json");
                    file.write(jsonArray.toJSONString());
                    file.flush();
                    file.close();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @PostMapping("delete/categories")
    public void deleteCategories(@RequestParam Long userId, @RequestBody String categoryListId) throws Exception {
        ArrayList<Long> IdListCategory = new ArrayList<>();
        for (String box : categoryListId.substring(1, categoryListId.length() - 1).split(",")) {
            IdListCategory.add(Long.parseLong(box));
        }
        ArrayList<String> nameListCategory = new ArrayList<>();
        for (long categoryId : IdListCategory) {
            Category categoryCurrent = categoryRepository.getOne(categoryId);
            nameListCategory.add(categoryCurrent.getName());
            categoryRepository.deleteById(categoryId);
        }
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/main/json/users.json"));
        List<JSONObject> users = jsonArray;
        CustomUser customUser = customUserRepository.getOne(userId);
        for (JSONObject user : users) {
            if (((String) user.get("login")).equals(customUser.getLogin())) {
                List<JSONObject> categories = (JSONArray) user.get("categories");
                List<JSONObject> listCategoriesDelete = new ArrayList<>();
                for (String currentNameCategory : nameListCategory) {
                    for (JSONObject category : categories) {
                        if (((String) category.get("name")).equals(currentNameCategory)) {
                            listCategoriesDelete.add(category);
                        }
                    }
                }
                for (JSONObject category : listCategoriesDelete) {
                    categories.remove(category);
                    FileWriter file = new FileWriter("./src/main/json/users.json");
                    file.write(jsonArray.toJSONString());
                    file.flush();
                    file.close();
                }
            }
        }
    }
}