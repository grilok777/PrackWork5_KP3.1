import java.util.concurrent.RecursiveTask;

class SearchTask extends RecursiveTask<Integer> {
    private int[][] array;
    private int startRow, endRow;

    public SearchTask(int[][] array, int startRow, int endRow) {
        this.array = array;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    protected Integer compute() {
        if (endRow - startRow <= 10) { // Базовий випадок
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    if (array[i][j] == i + j) {
                        //System.out.println("Steal:"+array[i][j]+"row - "+ i+" col - "+ j);
                        return array[i][j];
                    }
                }
            }
            return null;
        } else { // Розбиття задачі на підзадачі
            int mid = (startRow + endRow) / 2;
            SearchTask leftTask = new SearchTask(array, startRow, mid);
            SearchTask rightTask = new SearchTask(array, mid, endRow);
            leftTask.fork();
            Integer rightResult = rightTask.compute();
            Integer leftResult = leftTask.join();
            return leftResult != null ? leftResult : rightResult;
        }
    }
}