package main.stager.model;

import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
public class Contact extends FBModel {
    @NonNull @Getter private String name;
    @NonNull @Getter private String description;
    @NonNull @Getter private String email;
    @Setter  @Getter private boolean isOutgoing;
    @Setter  @Getter private boolean isIgnored;
}

