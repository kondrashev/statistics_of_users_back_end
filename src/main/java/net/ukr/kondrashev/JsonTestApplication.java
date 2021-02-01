package net.ukr.kondrashev;

import net.ukr.kondrashev.entities.*;
import net.ukr.kondrashev.repositories.CategoryRepository;
import net.ukr.kondrashev.repositories.CustomUserRepository;
import net.ukr.kondrashev.repositories.WordRepository;
import org.json.simple.parser.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.util.List;

@SpringBootApplication

public class JsonTestApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(JsonTestApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(final CustomUserRepository customUserRepository,
                                    final CategoryRepository categoryRepository,
                                    final WordRepository wordRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                JSONParser parser = new JSONParser();
                String str = "[{\"id\":2,\"login\":\"pavel\",\"password\":\"d033e22ae348aeb5660fc2140aec35850c4da997\",\"role\":\"USER\",\"categories\":[{\"id\":3,\"name\":\"Animals\",\"userName\":\"pavel\",\"words\":[{\"id\":4,\"name\":\"Tiger0\",\"meaning\":\"Тигр0\",\"userName\":\"pavel\"},{\"id\":5,\"name\":\"Tiger1\",\"meaning\":\"Тигр1\",\"userName\":\"pavel\"},{\"id\":6,\"name\":\"Tiger2\",\"meaning\":\"Тигр2\",\"userName\":\"pavel\"},{\"id\":7,\"name\":\"Tiger3\",\"meaning\":\"Тигр3\",\"userName\":\"pavel\"},{\"id\":8,\"name\":\"Tiger4\",\"meaning\":\"Тигр4\",\"userName\":\"pavel\"},{\"id\":9,\"name\":\"Tiger5\",\"meaning\":\"Тигр5\",\"userName\":\"pavel\"},{\"id\":10,\"name\":\"Tiger6\",\"meaning\":\"Тигр6\",\"userName\":\"pavel\"},{\"id\":11,\"name\":\"Tiger7\",\"meaning\":\"Тигр7\",\"userName\":\"pavel\"},{\"id\":12,\"name\":\"Tiger8\",\"meaning\":\"Тигр8\",\"userName\":\"pavel\"},{\"id\":13,\"name\":\"Tiger9\",\"meaning\":\"Тигр9\",\"userName\":\"pavel\"}]}]}]";
                JSONArray jar = (JSONArray) parser.parse(str);
                System.out.println(jar);
                try {
                    JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/main/json/users.json"));
                    List<JSONObject> users = jsonArray;
                    for (JSONObject user : users) {
                        CustomUser customUser = new CustomUser((String) user.get("login"),
                                (String) user.get("password"), UserRole.USER);
                        customUserRepository.save(customUser);
                        List<JSONObject> categories = (JSONArray) user.get("categories");
                        for (JSONObject currentCategory : categories) {
                            Category category = new Category(customUser, (String) currentCategory.get("name"), (String) currentCategory.get("userName"));
                            categoryRepository.save(category);
                            List<JSONObject> words = (JSONArray) currentCategory.get("words");
                            for (JSONObject currentWord : words) {
                                wordRepository.save(new Word(category, (String) currentWord.get("name"),
                                        (String) currentWord.get("meaning"), customUser.getLogin()));
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/");
    }
}
