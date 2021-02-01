package net.ukr.kondrashev.controllers;

import net.ukr.kondrashev.entities.CustomUser;
import net.ukr.kondrashev.repositories.CustomUserRepository;
import net.ukr.kondrashev.entities.UserRole;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomUserController {
    @Autowired
    private CustomUserRepository customUserRepository;

    @GetMapping("get/users")
    public List<CustomUser> doGetUsers(@RequestParam String pattern) {
        return customUserRepository.findByUser(pattern);
    }

    @PostMapping("add/user")
    public boolean addUser(@RequestBody CustomUser customUser) throws Exception {
        if (customUserRepository.existsByLogin(customUser.getLogin()) == false) {
            customUserRepository.save(new CustomUser(customUser.getLogin(), customUser.getPassword(), UserRole.USER));
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/main/json/users.json"));
            CustomUser currentUser = customUserRepository.findByLogin(customUser.getLogin());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", currentUser.getId());
            jsonObject.put("login", currentUser.getLogin());
            jsonObject.put("password", currentUser.getPassword());
            jsonObject.put("role", "user");
            List<JSONObject> listCategories = new ArrayList<>();
            jsonObject.put("categories", listCategories);
            jsonArray.add(jsonObject);
            FileWriter file = new FileWriter("./src/main/json/users.json");
            file.write(jsonArray.toJSONString());
            file.flush();
            file.close();
            return true;
        } else {
            return false;
        }
    }

    @PostMapping("delete/users")
    public void deleteUsers(@RequestBody String userListId) throws Exception {
        ArrayList<Long> IdListUser = new ArrayList<>();
        for (String box : userListId.substring(1, userListId.length() - 1).split(",")) {
            IdListUser.add(Long.parseLong(box));
        }
        ArrayList<String> loginListUser = new ArrayList<>();
        for (long userId : IdListUser) {
            CustomUser customUser = customUserRepository.getOne(userId);
            loginListUser.add(customUser.getLogin());
            customUserRepository.deleteById(userId);
        }
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/main/json/users.json"));
        List<JSONObject> users = jsonArray;
        List<JSONObject> listUsersDelete = new ArrayList<>();
        for (String currentLoginUser : loginListUser) {
            for (JSONObject user : users) {
                if (((String) user.get("login")).equals(currentLoginUser)) {
                    listUsersDelete.add(user);
                }
            }
        }
        for (JSONObject user : listUsersDelete) {
            users.remove(user);
            FileWriter file = new FileWriter("./src/main/json/users.json");
            file.write(jsonArray.toJSONString());
            file.flush();
            file.close();
        }
    }
}
