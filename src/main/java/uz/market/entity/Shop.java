package uz.market.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Shop {
    private String id;
    private String name;
    private List<Integer> ratings;
    private Long ownerId;
    private List<String> productIds;

    public double getRating() {
        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }
        return Math.round((sum / ratings.size()) * 10.0) / 10.0;
    }

    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            ratings.add(rating);
        }
    }
}
