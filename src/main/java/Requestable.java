import java.sql.ResultSet;

/**
 * Created by y.golota on 23.01.2017.
 */

@FunctionalInterface
public interface Requestable {
   String[] Execute(String... query);
}