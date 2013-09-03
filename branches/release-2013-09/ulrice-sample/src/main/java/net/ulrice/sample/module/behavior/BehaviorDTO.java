package net.ulrice.sample.module.behavior;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BehaviorDTO implements Serializable {

    private static final long serialVersionUID = -6056926423093751544L;

    private String firstname;
    private String lastname;
    private Gender gender;
    private final Set<String> occupation;
    private final List<KnowledgeDTO> knowledge;

    public BehaviorDTO() {
        super();

        occupation = new HashSet<String>();
        knowledge = new ArrayList<KnowledgeDTO>();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Set<String> getOccupation() {
        return occupation;
    }

    public List<KnowledgeDTO> getKnowledge() {
        return knowledge;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("BehaviorDTO {");
        if (firstname != null) {
            builder.append("\n\tFirstname: ");
            builder.append(firstname);
        }
        if (lastname != null) {
            builder.append("\n\tLastname: ").append(lastname);
        }
        if (gender != null) {
            builder.append("\n\tGender: ").append(gender);
        }
        if (occupation != null) {
            builder.append("\n\tOccupation: ").append(occupation);
        }
        if (knowledge != null) {
            builder.append("\n\tKnowledge: ").append(knowledge);
        }
        builder.append("\n}");

        return builder.toString();
    }

}
