const express = require('express');
const cors = require('cors');
const { GoogleGenAI } = require('@google/genai');

const app = express();
app.use(cors());
app.use(express.json());

// Inicializa el cliente de Gemini (usará la variable de entorno GEMINI_API_KEY)
const ai = new GoogleGenAI();

app.get('/', (req, res) => {
    res.send('El servidor backend de Pamela-ia con Gemini está activo.');
});

app.post('/api/chat', async (req, res) => {
    try {
        const userMessage = req.body.message || req.body.prompt || "";
        
        if (!userMessage) {
            return res.status(400).json({ reply: "Por favor, escribe un mensaje.", success: false });
        }

        // Llamada oficial al modelo de Gemini
        const response = await ai.models.generateContent({
            model: 'gemini-2.5-flash',
            contents: userMessage,
            config: {
                systemInstruction: "Eres Pamela IA, una asistente virtual amigable, inteligente y útil."
            }
        });

        const replyMessage = response.text || "No pude generar una respuesta en este momento.";

        res.json({ 
            reply: replyMessage,
            success: true 
        });
    } catch (error) {
        console.error("Error al conectar con Gemini:", error);
        res.status(500).json({ 
            reply: "Ocurrió un error al procesar tu mensaje con la IA.",
            success: false 
        });
    }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Servidor escuchando en el puerto ${PORT}`);
});
