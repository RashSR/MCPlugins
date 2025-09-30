package snowballarena;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

public enum Map{
	QUIDDITCH, NETHER, CHRISTMAS, SNOWARENA, SHRIEKING_SHACK, BEDWARS;
	
	private static final List<Map> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  	private static final int SIZE = VALUES.size();
  	private static final Random RANDOM = new Random();

  	public static Map randomMap()  {
    	return VALUES.get(RANDOM.nextInt(SIZE));
  	}
}