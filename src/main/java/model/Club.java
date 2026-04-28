package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Club {

    private Long id;
    private String title;
    private Category category;
    private String description;
    private String imageUrl;
    private List<Child> children;

    public Club(String title, Category category, String description, String imageUrl) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
