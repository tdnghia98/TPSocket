import javax.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String sender,
            time,
            content;
    private boolean isAnnouncement;

    public Message(String sender, String time, String content, boolean isAnnouncement) {
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
            formattedMessage = "~ ANNOUNCEMENT ~ " + time + content;
        } else {
            formattedMessage = time + " [" + sender + "] " + content;
        }
        return formattedMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
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
