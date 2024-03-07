package io.confluent.pie.csp_demo.aws.common.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private String user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;

}
