package net.ulrice.sample.module.behavior;

import java.io.Serializable;

public class KnowledgeDTO implements Serializable {

    private static final long serialVersionUID = 5866973535422526327L;

    private String knowledge;
    private String stars;
    private String comment;

    public KnowledgeDTO() {
        super();
    }

    public KnowledgeDTO(String knowledge, String stars, String comment) {
        super();

        this.knowledge = knowledge;
        this.stars = stars;
        this.comment = comment;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((comment == null) ? 0 : comment.hashCode());
        result = (prime * result) + ((knowledge == null) ? 0 : knowledge.hashCode());
        result = (prime * result) + ((stars == null) ? 0 : stars.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KnowledgeDTO other = (KnowledgeDTO) obj;
        if (comment == null) {
            if (other.comment != null) {
                return false;
            }
        }
        else if (!comment.equals(other.comment)) {
            return false;
        }
        if (knowledge == null) {
            if (other.knowledge != null) {
                return false;
            }
        }
        else if (!knowledge.equals(other.knowledge)) {
            return false;
        }
        if (stars == null) {
            if (other.stars != null) {
                return false;
            }
        }
        else if (!stars.equals(other.stars)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return knowledge + " (" + stars + "): " + comment;
    }

}
