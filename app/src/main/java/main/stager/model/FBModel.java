package main.stager.model;

import lombok.Getter;
import lombok.Setter;

public abstract class FBModel {
    @Getter @Setter
    protected String key;

    @Getter @Setter
    protected int pos = Integer.MAX_VALUE;
}
