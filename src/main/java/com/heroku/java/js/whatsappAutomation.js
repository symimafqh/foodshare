const qrcode = require('qrcode-terminal');
const { Client } = require('whatsapp-web.js');
const puppeteer = require('puppeteer-core');

// Create a new client with puppeteer configurations
const client = new Client({
    puppeteer: {
        // Specify the executablePath where Chrome/Chromium can be found in the Heroku environment
        executablePath: process.env.CHROME_BIN || '/usr/bin/google-chrome-stable',
        args: [
            '--no-sandbox',             // Required in Heroku to run Chromium without sandboxing
            '--disable-setuid-sandbox'  // Disables setuid sandbox
        ]
    }
});

// Listen for QR code generation
client.on('qr', (qr) => {
    // Generate and display the QR code in the terminal
    qrcode.generate(qr, { small: true });
});

// Listen for when the client is ready
client.on('ready', () => {
    console.log('WhatsApp client is ready!');

    // Retrieve command line arguments
    const args = process.argv.slice(2);
    const phoneNumber = args[0]; // The phone number to send to
    const message = args[1];      // The message to send

    // Send the message
    client.sendMessage(`${phoneNumber}@c.us`, message).then(response => {
        console.log(`Message sent to ${phoneNumber}: ${response}`);
        process.exit(0); // Exit the process after sending the message
    }).catch(err => {
        console.error('Error when sending message:', err);
        process.exit(1); // Exit with error
    });
});

// Initialize the WhatsApp client
client.initialize();
