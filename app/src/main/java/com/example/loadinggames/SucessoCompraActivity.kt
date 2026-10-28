
package com.example.loadinggames

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class SucessoCompraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sucesso_compra)

        val pedido = intent.getStringExtra("idCompra") ?: "-"
        val quantidade = intent.getIntExtra("quantidadeItens", 0)
        val total = intent.getDoubleExtra("totalCompra", 0.0)
        val forma = intent.getStringExtra("formaPagamento") ?: "Pix"

        findViewById<TextView>(R.id.txtNumeroPedido).text =
            "Pedido #$pedido"

        findViewById<TextView>(R.id.txtItensSucesso).text =
            if (quantidade == 1) "1 item" else "$quantidade itens"

        findViewById<TextView>(R.id.txtTotalSucesso).text =
            NumberFormat.getCurrencyInstance(
                Locale("pt", "BR")
            ).format(total)

        findViewById<TextView>(R.id.txtFormaSucesso).text =
            "Pagamento simulado via $forma"

        findViewById<Button>(R.id.btnIrBiblioteca)
            .setOnClickListener {
                val intent = Intent(
                    this,
                    BibliotecaActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }

        findViewById<Button>(R.id.btnContinuarSucesso)
            .setOnClickListener {
                val intent = Intent(
                    this,
                    MainActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
    }
}
