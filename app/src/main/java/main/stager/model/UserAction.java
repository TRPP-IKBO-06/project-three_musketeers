package main.stager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserAction extends FBModel {
    @Getter private Status status;
    @Getter private String name;
    @Setter @Getter private String owner;

    public UserAction(Status status, String name) {
        this.status = status;
        this.name = name;
    }
}

