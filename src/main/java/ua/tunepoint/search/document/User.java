package ua.tunepoint.search.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import static ua.tunepoint.search.config.Indices.USER_INDEX;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = USER_INDEX, createIndex = true)
@Setting(settingPath = "elastic/user/settings.json")
@Mapping(mappingPath = "elastic/user/mappings.json")
public class User {

    @Id
    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(type = FieldType.Text, name = "username")
    private String username;

    @Field(type = FieldType.Text, name = "first_name")
    private String firstName;

    @Field(type = FieldType.Text, name = "last_name")
    private String lastName;

    @Field(type = FieldType.Text, name = "pseudonym")
    private String pseudonym;

    @Field(type = FieldType.Text, name = "bio")
    private String bio;

    @Field(type = FieldType.Long, name = "follower_count")
    private Long followerCount;

    @Field(type = FieldType.Long, name = "following_count")
    private Long followingCount;

    @Field(type = FieldType.Long, name = "listening_count")
    private Long listeningCount;
}
