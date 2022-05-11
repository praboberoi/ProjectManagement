Feature: Restricting file type for profile picture to images

  Scenario: User tries to save an image as their profile picture (AC1, UUiii)
    Given User selects a "jpg"
    When The file is an accepted type
    Then Image is uploaded successfully


  Scenario: User tries to save a file as their profile picture (AC1, UUiii)
    Given User selects a "txt"
    When The file is not an accepted type
    Then Image is not uploaded successfully
