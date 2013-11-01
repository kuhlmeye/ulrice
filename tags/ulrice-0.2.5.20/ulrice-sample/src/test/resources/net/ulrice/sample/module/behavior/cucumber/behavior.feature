Feature:
	In order to demonstrate BDD with Ulrice
	As a developer
	I want to get a first impression
	
Background:
	Given the sample application is running
	
Scenario:
	Given the module "Behavior Driven Development" is open
	When I enter "Jane" into "Firstname"
	And I enter "Doe" into "Lastname"
	And I click the radio button "Female"
	And I select "Other" in "Occupation"
	And I enter following knowledge into the table:
		| Knowledge   | Stars   | Comment         |
		| Java        | *****   | Half of my life |
		| Ruby        |         | Don't know yet  |
		| ASM         | ***     | Hirnwixerei     |
	And I execute the action "Save"
	Then a dialog should appear containing following data:
		| Jane            |
		| Doe             |
		| FEMALE          |
		| Java            |
		| Don't know yet  |


