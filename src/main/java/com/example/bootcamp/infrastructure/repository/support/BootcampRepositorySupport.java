package com.example.bootcamp.infrastructure.repository.support;

import java.time.LocalDate;

public final class BootcampRepositorySupport {

  private BootcampRepositorySupport() {
  }

  public static final String COLUMN_BOOTCAMP_ID = "bootcamp_id";
  public static final String COLUMN_BOOTCAMP_NAME = "bootcamp_name";
  public static final String COLUMN_BOOTCAMP_DESCRIPTION = "bootcamp_description";
  public static final String COLUMN_BOOTCAMP_LAUNCH_DATE = "bootcamp_launch_date";
  public static final String COLUMN_BOOTCAMP_DURATION_WEEKS = "bootcamp_duration_weeks";
  public static final String COLUMN_CAPABILITY_COUNT = "capability_count";
  public static final String COLUMN_CAPABILITY_ID = "capability_id";
  public static final String COLUMN_CAPABILITY_NAME = "capability_name";
  public static final String COLUMN_CAPABILITY_DESCRIPTION = "capability_description";
  public static final String COLUMN_TECHNOLOGY_ID = "technology_id";
  public static final String COLUMN_TECHNOLOGY_NAME = "technology_name";

  public static final String BASE_SELECT = """
      SELECT b.id AS bootcamp_id,
             b.name AS bootcamp_name,
             b.description AS bootcamp_description,
             b.launch_date AS bootcamp_launch_date,
             b.duration_weeks AS bootcamp_duration_weeks,
             bc.capability_id AS capability_id
      FROM bootcamp.bootcamps b
      LEFT JOIN bootcamp.bootcamp_capability bc ON bc.bootcamp_id = b.id
      """;

  public static final String PAGINATED_SELECT_TEMPLATE = """
      WITH bootcamp_counts AS (
          SELECT b.id,
                 b.name,
                 b.description,
                 b.launch_date,
                 b.duration_weeks,
                 COUNT(DISTINCT bc.capability_id) AS capability_count
          FROM bootcamp.bootcamps b
          LEFT JOIN bootcamp.bootcamp_capability bc ON bc.bootcamp_id = b.id
          GROUP BY b.id, b.name, b.description, b.launch_date, b.duration_weeks
      ),
      paged_bootcamps AS (
          SELECT id,
                 name,
                 description,
                 launch_date,
                 duration_weeks,
                 capability_count
          FROM bootcamp_counts
          ORDER BY %s %s, id ASC
          LIMIT :limit OFFSET :offset
      )
      SELECT pb.id AS bootcamp_id,
             pb.name AS bootcamp_name,
             pb.description AS bootcamp_description,
             pb.launch_date AS bootcamp_launch_date,
             pb.duration_weeks AS bootcamp_duration_weeks,
             pb.capability_count AS capability_count,
             c.id AS capability_id,
             c.name AS capability_name,
             c.description AS capability_description,
             t.id AS technology_id,
             t.name AS technology_name
      FROM paged_bootcamps pb
      LEFT JOIN bootcamp.bootcamp_capability bc ON bc.bootcamp_id = pb.id
      LEFT JOIN bootcamp.capabilities c ON c.id = bc.capability_id
      LEFT JOIN bootcamp.capability_technology ct ON ct.capability_id = c.id
      LEFT JOIN bootcamp.technologies t ON t.id = ct.technology_id
      ORDER BY %s %s, pb.id ASC, c.name ASC, c.id ASC, t.name ASC, t.id ASC
      """;

  public static record BootcampCapabilityRow(
      String bootcampId,
      String bootcampName,
      String bootcampDescription,
      LocalDate launchDate,
      Integer durationWeeks,
      String capabilityId
  ) {
  }

  public static record BootcampCapabilityTechnologyDetailRow(
      String bootcampId,
      String bootcampName,
      String bootcampDescription,
      LocalDate launchDate,
      Integer durationWeeks,
      int capabilityCount,
      String capabilityId,
      String capabilityName,
      String capabilityDescription,
      String technologyId,
      String technologyName
  ) {
  }
}
