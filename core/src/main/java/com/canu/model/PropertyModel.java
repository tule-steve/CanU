package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data

@Table(name = "property")
public class PropertyModel {

    public enum Type {
        RATING_CRITERIA,
        POINT_EXCHANGE
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    @NotBlank(message = "key is required")
    @Column(name = "field")
    String key;

    @Column(name = "value")
    String property;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    Type type;

    @Transient
    @Enumerated(EnumType.STRING)
    CountryModel.Locale locale;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "property")
    List<UserPropertyModel> userAssoc = new ArrayList<>();


    @JsonIgnore
    @ElementCollection
    @MapKeyColumn(name = "field")
    @CollectionTable(name = "multi_properties", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name="property")
    private Map<String, String> positions = new HashMap<>();

    @Transient
    Integer value = 0;

//    @JsonAnyGetter
//    public Map<String,String> getPositions() {
//        return positions;
//    }

    public void updateValue(long userId, String locale) {
        try {
            this.setLocale(CountryModel.Locale.valueOf(locale));
        } catch (Exception ex) {

        }
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        userAssoc.stream()
                 .filter(r -> r.getUser() != null && r.getUser().getId().equals(userId))
                 .findFirst()
                 .ifPresent(r -> {
                     sb.append(":");
                     sb.append(r.getId());
                     value = r.getRating();
                     property = positions.get(locale);
                 });
        key = sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyModel)) {
            return false;
        }
        return id != null && id.equals(((PropertyModel) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
