package sber.optim.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PersonDTO {
    private int id;
    private String firstName;
    private String secondName;
    private int age;
    private boolean activeProfile;
}
