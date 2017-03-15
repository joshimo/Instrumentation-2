import java.sql.SQLException;
import java.util.Set;

/**
 * Created by y.golota on 20.01.2017.
 */

@FunctionalInterface
public interface ExecutableSet {
    void Execute(Set<String> query);
}