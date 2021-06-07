package main.stager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Contact extends FBModel {
    @Getter private String name;
    @Getter private String description;
    @Getter private String email;
    @Setter @Getter private boolean isIncoming;
    @Setter @Getter private boolean isOutgoing;
    @Setter @Getter private boolean isIgnored;

    public Contact(String name, String description, String email) {
        this.name = name;
        this.description = description;
        this.email = email;
    }
}

