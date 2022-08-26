package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    private long id;
    @NotNull
    @Email(message = "Email должен быть корректным адресом электронной почты")
    private String email;
    @NotBlank(message = "логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "дата рождения не может быть в будущем")
    @NotNull
    private LocalDate birthday;
}

