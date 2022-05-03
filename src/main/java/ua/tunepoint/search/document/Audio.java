package ua.tunepoint.search.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import ua.tunepoint.search.config.Indices;

@Data
@Document(indexName = Indices.AUDIO_INDEX, createIndex = true)
@Mapping(mappingPath = "elastic/audio/mappings.json")
@Setting(settingPath = "elastic/audio/settings.json")
public class Audio {

    @Id
    @Field(type = FieldType.Keyword, name = "id")
    private Long id;

    @Field(type = FieldType.Keyword, name = "owner_id")
    private Long ownerId;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Text, name = "author_pseudonym")
    private String authorPseudonym;

    @Field(type = FieldType.Long, name = "listening_count")
    private Long listeningCount;

    @Field(type = FieldType.Long, name = "like_count")
    private Long likeCount;

    @Field(type = FieldType.Boolean, name = "is_private")
    private Boolean isPrivate;
}
