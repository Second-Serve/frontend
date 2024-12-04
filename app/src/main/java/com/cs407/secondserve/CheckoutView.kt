import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cs407.secondserve.PaymentView
import com.cs407.secondserve.R

class CheckoutView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val checkoutButton: Button = findViewById(R.id.checkout_button)

        checkoutButton.setOnClickListener {
            val intent = Intent(this, PaymentView::class.java)
            startActivity(intent)
        }
    }
}