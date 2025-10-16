const bodyDataset = document.body.dataset;
const DEFAULT_API_BASE = window.location.origin.startsWith('http')
  ? window.location.origin
  : 'http://localhost:3001';
const API_BASE_URL = bodyDataset.apiBaseUrl || DEFAULT_API_BASE;

const selectors = {
  productGrid: document.getElementById('product-grid'),
  filterStrength: document.getElementById('filter-strength'),
  filterFlavor: document.getElementById('filter-flavor'),
  filterNicotine: document.getElementById('filter-nicotine'),
  filterApply: document.getElementById('filter-apply'),
  storyHeadline: document.getElementById('story-headline'),
  storyMission: document.getElementById('story-mission'),
  storyTimeline: document.getElementById('story-timeline'),
  insightGrid: document.getElementById('insight-grid'),
  contactForm: document.getElementById('contact-form'),
  contactFeedback: document.querySelector('.form-feedback'),
  currentYear: document.getElementById('current-year'),
};

selectors.currentYear.textContent = new Date().getFullYear();

async function fetchJson(endpoint, options = {}) {
  const url = new URL(endpoint, API_BASE_URL);

  if (options.params) {
    Object.entries(options.params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.set(key, value);
      }
    });
  }

  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...options,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}));
    const message = errorBody.error || 'Não foi possível concluir a solicitação.';
    throw new Error(message);
  }

  return response.json();
}

function renderProducts(products = []) {
  const grid = selectors.productGrid;
  grid.innerHTML = '';

  if (!products.length) {
    const empty = document.createElement('p');
    empty.className = 'product-grid__empty';
    empty.textContent = 'Nenhum produto encontrado com os filtros atuais.';
    grid.appendChild(empty);
    return;
  }

  products.forEach((product) => {
    const card = document.createElement('article');
    card.className = 'product-card';
    card.innerHTML = `
      <span class="product-card__badge">${product.strength}</span>
      <h3 class="product-card__title">${product.name}</h3>
      <p class="product-card__description">${product.description}</p>
      <div class="product-card__meta">
        <span>${product.nicotineMg}mg</span>
        <span>${product.flavor}</span>
        <span>R$ ${product.price.toFixed(2)}</span>
        <span>Estoque: ${product.inventory}</span>
      </div>
    `;
    grid.appendChild(card);
  });
}

function renderStory(story) {
  if (!story) return;

  selectors.storyHeadline.textContent = story.headline;
  selectors.storyMission.textContent = story.mission || '';
  selectors.storyTimeline.innerHTML = '';

  story.milestones.forEach((milestone) => {
    const item = document.createElement('article');
    item.className = 'story-timeline__item';
    item.innerHTML = `
      <span class="story-timeline__year">${milestone.year}</span>
      <p class="story-timeline__summary">${milestone.summary}</p>
    `;
    selectors.storyTimeline.appendChild(item);
  });
}

function renderInsights(insights = {}) {
  selectors.insightGrid.innerHTML = '';

  const items = [
    { label: 'Saches em estoque', value: insights.totalInventory ?? '—' },
    {
      label: 'Força média (mg)',
      value:
        insights.averageStrengthMg !== undefined && insights.averageStrengthMg !== null
          ? `${insights.averageStrengthMg} mg`
          : '—',
    },
    {
      label: 'Produto destaque',
      value: insights.topSeller?.name || '—',
    },
  ];

  items.forEach(({ label, value }) => {
    const wrapper = document.createElement('div');
    wrapper.className = 'insight-card';

    const dt = document.createElement('dt');
    dt.textContent = label;
    const dd = document.createElement('dd');
    dd.textContent = value;

    wrapper.append(dt, dd);
    selectors.insightGrid.appendChild(wrapper);
  });
}

async function loadInitialData() {
  try {
    const [productsResponse, storyResponse, insightsResponse] = await Promise.all([
      fetchJson('/api/products'),
      fetchJson('/api/story'),
      fetchJson('/api/insights'),
    ]);

    renderProducts(productsResponse.data);
    renderStory(storyResponse.data);
    renderInsights(insightsResponse.data);
  } catch (error) {
    console.error('Erro ao carregar dados iniciais', error);
    const notice = document.createElement('p');
    notice.textContent = 'Não foi possível conectar à API da Nyx.';
    notice.className = 'product-grid__empty';
    selectors.productGrid.appendChild(notice);
  }
}

function collectFilters() {
  return {
    strength: selectors.filterStrength.value,
    flavor: selectors.filterFlavor.value.trim(),
    maxNicotine: selectors.filterNicotine.value,
  };
}

selectors.filterApply.addEventListener('click', async () => {
  try {
    const filters = collectFilters();
    const response = await fetchJson('/api/products', { params: filters });
    renderProducts(response.data);
  } catch (error) {
    renderProducts([]);
    showFeedback(error.message, true);
  }
});

function showFeedback(message, isError = false) {
  selectors.contactFeedback.textContent = message;
  selectors.contactFeedback.classList.toggle('form-feedback--error', isError);
  selectors.contactFeedback.classList.toggle('form-feedback--success', !isError);
}

selectors.contactForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.target);
  const payload = Object.fromEntries(formData.entries());

  if (!payload.name || !payload.email || !payload.message) {
    showFeedback('Preencha os campos obrigatórios.', true);
    return;
  }

  showFeedback('Enviando mensagem…');

  try {
    const response = await fetchJson('/api/contact', {
      method: 'POST',
      body: JSON.stringify(payload),
    });

    showFeedback('Recebemos sua mensagem. Em breve entraremos em contato.');
    event.target.reset();

    const record = response.data;
    record && console.info('Contato registrado', record);
  } catch (error) {
    showFeedback(error.message, true);
  }
});

loadInitialData();
