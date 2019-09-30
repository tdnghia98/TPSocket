import javax.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int sender;
    private String time,
            content;
    private boolean isAnnouncement;

    public Message(int sender, String time, String content, boolean isAnnouncement) {
        this.sender = sender;
        this.time = time;
        this.content = content;
        this.isAnnouncement = isAnnouncement;
    }

    public Message() {
    }

    public String format() {
        String formattedMessage = "";
        if (isAnnouncement) {
            formattedMessage = "\n\n~ ANNOUNCEMENT ~ " + time + content + "\n\n";
        } else {
            formattedMessage = time + " [" + sender + "] " + content;
        }
        return formattedMessage;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAnnouncement() {
        return isAnnouncement;
    }

    public void setAnnouncement(boolean announcement) {
        isAnnouncement = announcement;
    }
}
