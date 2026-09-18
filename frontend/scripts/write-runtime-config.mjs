import { writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';

const apiUrl = (process.env.MEDFEMA_API_URL || '/api').trim().replace(/\/$/, '');
const output = fileURLToPath(new URL('../public/config.js', import.meta.url));

writeFileSync(output, `window.MEDFEMA_CONFIG = ${JSON.stringify({ apiUrl })};\n`, 'utf8');
console.log(`Configuração da API: ${apiUrl}`);
