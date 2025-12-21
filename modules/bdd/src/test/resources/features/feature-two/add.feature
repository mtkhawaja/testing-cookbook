Feature: Feature Two Example

  Scenario: Action Four sums the provided values
    Given the following values:
      | label | value |
      | a     | 10    |
      | b     | 20    |
      | c     | 30    |
    When feature two sums the values
    Then the result should be 60