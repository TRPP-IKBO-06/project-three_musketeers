package main.stager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
public class UserAction extends FBModel {
    @NonNull @Getter private Status status;
    @NonNull @Getter private String name;
    @Setter @Getter private String owner;
}

