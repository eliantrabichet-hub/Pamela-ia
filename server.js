const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
    res.send('El servidor backend de Pamela-ia está activo.');
});

app.post('/api/chat', (req, res) => {
    try {
        const userMessage = req.body.message || req.body.prompt || "";
        const replyMessage = `Hola, soy Pamela. Recibí tu mensaje: "${userMessage}". ¡El servidor responde correctamente!`;

        res.json({ 
            reply: replyMessage,
            success: true 
        });
    } catch (error) {
        res.status(500).json({ 
            reply: "Ocurrió un error en el servidor.",
            success: false 
        });
    }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Servidor escuchando en el puerto ${PORT}`);
});
