import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.*

class GmailSender(private val user: String, private val password: String) {

    fun sendMail(subject: String, body: String, recipient: String, filePath: String?) {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(user, password)
            }
        })

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(user))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))
            message.subject = subject

            val multipart = MimeMultipart()

            // Nội dung email
            val textPart = MimeBodyPart()
            textPart.setText(body)
            multipart.addBodyPart(textPart)

            // Đính kèm file
            if (filePath != null) {
                val attachmentPart = MimeBodyPart()
                val source = FileDataSource(filePath)
                attachmentPart.dataHandler = DataHandler(source)
                attachmentPart.fileName = source.name
                multipart.addBodyPart(attachmentPart)
            }

            message.setContent(multipart)

            Transport.send(message)
            println("Email sent successfully")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}
