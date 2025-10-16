const http = require('http');
const { URL } = require('url');

const PORT = process.env.PORT || 3001;
const ALLOWED_ORIGINS = process.env.ALLOWED_ORIGINS
  ? process.env.ALLOWED_ORIGINS.split(',').map((origin) => origin.trim())
  : ['*'];

const products = [
  {
    id: 'aurora-mint-6mg',
    name: 'Aurora Mint 6mg',
    nicotineMg: 6,
    flavor: 'Mint',
    description:
      'Sachets refrescantes com menta cristalina e notas leves de eucalipto para uma experiência equilibrada.',
    price: 22.9,
    inventory: 180,
    strength: 'Suave',
  },
  {
    id: 'lunar-berry-12mg',
    name: 'Lunar Berry 12mg',
    nicotineMg: 12,
    flavor: 'Frutas Vermelhas',
    description:
      'Mistura intensa de amora, framboesa e um toque de anis para quem prefere sabores marcantes.',
    price: 24.5,
    inventory: 120,
    strength: 'Moderado',
  },
  {
    id: 'solar-citrus-18mg',
    name: 'Solar Citrus 18mg',
    nicotineMg: 18,
    flavor: 'Cítrico',
    description:
      'Perfil cítrico vibrante com limão siciliano e grapefruit para impactar com frescor e potência.',
    price: 26.9,
    inventory: 90,
    strength: 'Intenso',
  },
];

const story = {
  headline: 'Experiências refinadas de nicotina para o cotidiano urbano.',
  mission:
    'A Nyx cria saches de nicotina premium que combinam tecnologia farmacêutica com perfis sensoriais sofisticados, oferecendo alternativas discretas e consistentes.',
  milestones: [
    { year: 2020, summary: 'Fundação em São Paulo com foco em pesquisa de formulações de liberação controlada.' },
    { year: 2021, summary: 'Primeira linha Aurora ganha destaque no mercado nacional de substituição do cigarro.' },
    { year: 2023, summary: 'Expansão para a linha Solar com blends cítricos e cápsulas inteligentes de umidade.' },
  ],
};

const contactMessages = [];

function buildResponseHeaders(origin) {
  if (ALLOWED_ORIGINS.includes('*')) {
    return {
      'Content-Type': 'application/json; charset=utf-8',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': 'Content-Type',
      'Access-Control-Allow-Methods': 'GET,POST,OPTIONS',
    };
  }

  const allowed = ALLOWED_ORIGINS.includes(origin) ? origin : ALLOWED_ORIGINS[0];
  return {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': allowed,
    'Access-Control-Allow-Headers': 'Content-Type',
    'Access-Control-Allow-Methods': 'GET,POST,OPTIONS',
  };
}

function sendJson(res, status, payload, origin = '*') {
  const headers = buildResponseHeaders(origin);
  res.writeHead(status, headers);
  res.end(JSON.stringify(payload));
}

function parseBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', (chunk) => {
      body += chunk;
      if (body.length > 1e6) {
        req.connection.destroy();
        reject(new Error('Payload too large'));
      }
    });

    req.on('end', () => {
      if (!body) {
        resolve({});
        return;
      }

      try {
        resolve(JSON.parse(body));
      } catch (error) {
        reject(new Error('Invalid JSON body'));
      }
    });

    req.on('error', (error) => reject(error));
  });
}

function filterProducts(url) {
  const searchParams = url.searchParams;
  let filtered = [...products];

  if (searchParams.has('strength')) {
    const strength = searchParams.get('strength').toLowerCase();
    filtered = filtered.filter((product) => product.strength.toLowerCase() === strength);
  }

  if (searchParams.has('flavor')) {
    const flavor = searchParams.get('flavor').toLowerCase();
    filtered = filtered.filter((product) => product.flavor.toLowerCase().includes(flavor));
  }

  if (searchParams.has('maxNicotine')) {
    const maxNicotine = Number(searchParams.get('maxNicotine'));
    if (!Number.isNaN(maxNicotine)) {
      filtered = filtered.filter((product) => product.nicotineMg <= maxNicotine);
    }
  }

  return filtered;
}

const server = http.createServer(async (req, res) => {
  const origin = req.headers.origin || '*';

  if (req.method === 'OPTIONS') {
    const headers = buildResponseHeaders(origin);
    res.writeHead(204, headers);
    res.end();
    return;
  }

  const requestUrl = new URL(req.url, `http://${req.headers.host}`);

  if (req.method === 'GET' && requestUrl.pathname === '/api/products') {
    const response = filterProducts(requestUrl);
    sendJson(res, 200, { data: response, total: response.length }, origin);
    return;
  }

  if (req.method === 'GET' && requestUrl.pathname.startsWith('/api/products/')) {
    const productId = requestUrl.pathname.replace('/api/products/', '');
    const product = products.find((item) => item.id === productId);

    if (!product) {
      sendJson(res, 404, { error: 'Produto não encontrado' }, origin);
      return;
    }

    sendJson(res, 200, { data: product }, origin);
    return;
  }

  if (req.method === 'GET' && requestUrl.pathname === '/api/story') {
    sendJson(res, 200, { data: story }, origin);
    return;
  }

  if (req.method === 'GET' && requestUrl.pathname === '/api/insights') {
    const insights = {
      totalInventory: products.reduce((sum, product) => sum + product.inventory, 0),
      averageStrengthMg: Math.round(
        products.reduce((sum, product) => sum + product.nicotineMg, 0) / products.length
      ),
      topSeller: products.reduce((prev, current) =>
        prev.inventory > current.inventory ? prev : current
      ),
    };

    sendJson(res, 200, { data: insights }, origin);
    return;
  }

  if (req.method === 'POST' && requestUrl.pathname === '/api/contact') {
    try {
      const payload = await parseBody(req);
      const { name, email, message, phone } = payload;

      if (!name || !email || !message) {
        sendJson(
          res,
          400,
          { error: 'Campos obrigatórios: name, email e message.' },
          origin
        );
        return;
      }

      const record = {
        name,
        email,
        phone: phone || null,
        message,
        receivedAt: new Date().toISOString(),
      };
      contactMessages.push(record);

      sendJson(res, 201, { data: record, message: 'Contato registrado com sucesso.' }, origin);
    } catch (error) {
      sendJson(res, 400, { error: error.message }, origin);
    }
    return;
  }

  sendJson(res, 404, { error: 'Rota não encontrada' }, origin);
});

if (require.main === module) {
  server.listen(PORT, () => {
    console.log(`Nyx API disponível na porta ${PORT}`);
  });
}

module.exports = { server, products, story, contactMessages };
