package net.ulrice.sample.module.databinding;

public class PersonGenerator {

	private static final String LASTNAME_ARR[] = new String[] {
		"SMITH", "JOHNSON", "WILLIAMS", "JONES", "BROWN", "DAVIS", "MILLER", "WILSON", "MOORE", "TAYLOR", "ANDERSON", "THOMAS", "JACKSON", "WHITE", "HARRIS",
		"MARTIN","THOMPSON", "GARCIA", "MARTINEZ", "ROBINSON", "CLARK", "RODRIGUEZ", "LEWIS", "LEE", "WALKER", "HALL", "ALLEN", "YOUNG", "HERNANDEZ", "KING", "WRIGHT", "LOPEZ",  "HILL",
		"SCOTT", "GREEN", "ADAMS", "BAKER", "GONZALEZ", "NELSON", "CARTER", "MITCHELL", "PEREZ", "ROBERTS", "TURNER", "PHILLIPS", "CAMPBELL", "PARKER", "EVANS", "EDWARDS","COLLINS",
		"STEWART", "SANCHEZ", "MORRIS", "ROGERS", "REED", "COOK", "MORGAN", "BELL", "MURPHY", "BAILEY", "RIVERA", "COOPER", "RICHARDSON", "COX", "HOWARD", "WARD", "TORRES",
		"PETERSON", "GRAY", "RAMIREZ", "JAMES", "WATSON", "BROOKS", "KELLY", "SANDERS", "PRICE", "BENNETT", "WOOD", "BARNES", "ROSS", "HENDERSON", "COLEMAN", "JENKINS", "PERRY",
		"POWELL", "LONG", "PATTERSON", "HUGHES", "FLORES", "WASHINGTON", "BUTLER", "SIMMONS", "FOSTER", "GONZALES", "BRYANT", "ALEXANDER", "RUSSELL", "GRIFFIN", "DIAZ", "HAYES"
		};
	
	private static final String FIRSTNAME_ARR[] = new String[] {
		"JAMES", "JOHN", "ROBERT", "MICHAEL", "WILLIAM", "DAVID", "RICHARD", "CHARLES", "JOSEPH",
		"THOMAS", "CHRISTOPHER", "DANIEL", "PAUL", "MARK", "DONALD", "GEORGE", "KENNETH", "STEVEN", "EDWARD", "BRIAN", "RONALD", "ANTHONY", "KEVIN", "JASON",
		"MATTHEW", "GARY", "TIMOTHY", "JOSE", "LARRY", "JEFFREY", "FRANK", "SCOTT", "ERIC", "STEPHEN", "ANDREW", "RAYMOND", "GREGORY", "JOSHUA", "JERRY",
		"DENNIS", "WALTER", "PATRICK", "PETER", "HAROLD", "DOUGLAS", "HENRY", "CARL", "ARTHUR", "RYAN", "ROGER", "JOE", "JUAN", "JACK", "ALBERT", "JONATHAN",
		"JUSTIN", "TERRY", "GERALD", "KEITH", "SAMUEL", "WILLIE", "RALPH", "LAWRENCE", "NICHOLAS", "ROY", "BENJAMIN", "BRUCE", "BRANDON", "ADAM",  "HARRY",
		"FRED", "WAYNE", "BILLY", "STEVE", "LOUIS", "JEREMY", "AARON", "RANDY", "HOWARD", "EUGENE", "CARLOS", "RUSSELL", "BOBBY", "VICTOR", "MARTIN",
		"ERNEST", "PHILLIP", "TODD", "JESSE", "CRAIG", "ALAN", "SHAWN", "CLARENCE", "SEAN","PHILIP", "CHRIS", "JOHNNY", "EARL", "JIMMY", "ANTONIO",
		"MARY", "PATRICIA", "LINDA", "BARBARA", "ELIZABETH", "JENNIFER", "MARIA", "SUSAN", "MARGARET", "DOROTHY", "LISA", "NANCY", "KAREN", "BETTY",
		"HELEN", "SANDRA", "DONNA", "CAROL", "RUTH", "SHARON", "MICHELLE", "LAURA", "SARAH", "KIMBERLY", "DEBORAH", "JESSICA", "SHIRLEY", "CYNTHIA", "ANGELA", "MELISSA", "BRENDA",
		"AMY", "ANNA", "REBECCA", "VIRGINIA", "KATHLEEN", "PAMELA", "MARTHA", "DEBRA", "AMANDA", "STEPHANIE", "CAROLYN", "CHRISTINE", "MARIE", "JANET", "CATHERINE",  "FRANCES",
		"ANN", "JOYCE", "DIANE", "ALICE", "JULIE", "HEATHER", "TERESA", "DORIS", "GLORIA", "EVELYN", "JEAN", "CHERYL", "MILDRED", "KATHERINE", "JOAN", "ASHLEY", "JUDITH", "ROSE",
		"JANICE", "KELLY", "NICOLE", "JUDY", "CHRISTINA", "KATHY", "THERESA", "BEVERLY", "DENISE", "TAMMY", "IRENE", "JANE", "LORI", "RACHEL", "MARILYN", "ANDREA", "KATHRYN",
		"LOUISE", "SARA", "ANNE", "JACQUELINE", "WANDA", "BONNIE", "JULIA", "RUBY", "LOIS", "TINA", "PHYLLIS", "NORMA", "PAULA", "DIANA", "ANNIE", "LILLIAN", "EMILY", "ROBIN"
		};
	
	public static final String STREET_ARR[] = new String[] {
		"Main Street", "Church Street", "High Street", "Elm Street", "Chestnut Street", "Walnut Street", "Maple Street", "Washington Street", "2nd Street", "Broad Street", "Center Street", "Maple Avenue", 
		"Park Avenue", "South Street", "Pine Street", "Water Street", "Market Street", "Oak Street", "School Street", "Union Street", "North Street", "Spring Street", "River Road", "Court Street", 
		"Prospect Street", "Park Street", "3rd Street", "Cedar Street", "Front Street", "Cherry Street", "Washington Avenue", "Franklin Street", "Spruce Street", "West Street", "Central Avenue"
	};
	
	public static final String CITY_NAME[] = new String[] {
		"Midway", "Fairview", "Oak Grove", "Five Points", "Riverside", "Pleasant Hill", "Mount Pleasant", "Bethel", "Centerville", 
		"New Hope", "Liberty", "Oakland", "Union", "Pleasant Valley", "Shady Grove", "Pine Grove", "Salem", "Greenwood", "Pleasant Grove", 
		"Forest Hills", "Oak Hill", "Georgetown", "Lakeview", "Shiloh", "Glendale", "Lakewood", "Concord", "Cedar Grove", "Highland Park"
	};
	
    public static Person createRandomPerson() {
    	Person person = new Person();
    	person.setFirstName(new String(FIRSTNAME_ARR[(int)(Math.random()* FIRSTNAME_ARR.length)]));
    	person.setLastName(new String(LASTNAME_ARR[(int)(Math.random()* LASTNAME_ARR.length)]));
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append(Math.round(Math.random()*100)).append(" ");
    	builder.append(new String(STREET_ARR[(int)(Math.random()* STREET_ARR.length)])).append("\n");
    	builder.append(Math.round(Math.random()*100000)).append(" ");
    	builder.append(new String(CITY_NAME[(int)(Math.random()* CITY_NAME.length)]));
    	person.setAddress(builder.toString());
    	person.setAge((int)(Math.random() * 100));
    	
    	return person;
    }
}