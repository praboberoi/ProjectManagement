Feature: Restricting file type for profile picture to images

  Scenario Outline: User tries to save a photo as their profile picture (AC1, UUiii)
    Given User selects browse under profile photo
    When User selects an <imageFile>
    Then Error message should be <error>
    Examples:
    |imageFile| error|
    | ".jpg" | "null" |

  Scenario Outline: User tries to save a file as their profile picture (AC1, UUiii)
    Given User selects browse under profile photo
    When User selects an <imageFile>
    Then Account page should display <error>
    Examples:
    |imageFile| error|
    | ".txt" | "File type must be jpg" |
