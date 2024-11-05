package interfaces;

import java.util.List;

/**
 * Interface for loading data from loader fileS.
 * Implementing classes should define how data is loaded
 * from different types of sources.
 */
public interface DataLoader {

    /**
     * Loads data from the specified file path.
     *
     * @param filePath the path to the file from which to load data
     * @return a List containing the loaded data
     */
    default List loadData(String filePath){return null;};
}
