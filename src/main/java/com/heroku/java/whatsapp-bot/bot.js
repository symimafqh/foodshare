const puppeteer = require('puppeteer');

async function initWhatsApp() {
    const browser = await puppeteer.launch({ headless: false });
    const page = await browser.newPage();
    await page.goto('https://web.whatsapp.com', { waitUntil: 'networkidle0' });
    await page.waitForSelector('div[data-testid="chat-list"]', { timeout: 60000 });
    console.log('WhatsApp Web loaded successfully.');
    return page;
}

async function sendMessage(page, contactNumber, message) {
    const searchSelector = 'div[contenteditable="true"][data-tab="3"]';
    await page.waitForSelector(searchSelector);
    await page.click(searchSelector);
    await page.type(searchSelector, contactNumber);
    await page.waitForTimeout(2000);

    const contactSelector = `span[title="${contactNumber}"]`;
    await page.waitForSelector(contactSelector, { timeout: 5000 });
    await page.click(contactSelector);

    const messageSelector = 'div[contenteditable="true"][data-tab="10"]';
    await page.waitForSelector(messageSelector);
    await page.click(messageSelector);
    await page.type(messageSelector, message);
    await page.keyboard.press('Enter');

    console.log(`Message sent to ${contactNumber}: ${message}`);
}

async function main() {
    const message = process.argv[2]; // Get the message from command line arguments
    const studentNumbers = process.argv[3].split(','); // Get the phone numbers

    try {
        const page = await initWhatsApp();

        for (const contact of studentNumbers) {
            await sendMessage(page, contact.trim(), message);
        }

        // Keep the script running to avoid closing
        await new Promise(() => {});
    } catch (error) {
        console.error('An error occurred:', error);
    }
}

main();
