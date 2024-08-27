import java.util.Objects;

public class Task {
    private final  String title;
    private final String description;
    private final int idNum;
    private TaskStatus taskStatus;

    public Task(String title, String description, int idNum) {
        this.title = title;
        this.description = description;
        this.idNum = idNum;
        this.taskStatus = TaskStatus.NEW;
    }

    public String getTitle() {
        return title;
    }

    public int getIdNum() {
        return idNum;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idNum == task.idNum && Objects.equals(getTitle(), task.getTitle()) && Objects.equals(getDescription(), task.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNum);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idNum=" + idNum +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
