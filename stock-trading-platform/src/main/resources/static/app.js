// ===== STATE =====
const API = '';        // same-origin, Spring Boot serves static files
let currentUser = null;
let selectedStock = null;
let allStocks = [];
let refreshTimer = null;

// ===== AUTH =====

function switchTab(tab) {
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  event.target.classList.add('active');
  document.getElementById('login-form').classList.toggle('hidden', tab !== 'login');
  document.getElementById('register-form').classList.toggle('hidden', tab !== 'register');
  clearError();
}

async function login() {
  const username = document.getElementById('login-username').value.trim();
  const password = document.getElementById('login-password').value.trim();
  if (!username || !password) return showError('auth-error', 'Please fill all fields');

  try {
    const res = await fetch(`${API}/api/users/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    const data = await res.json();
    if (!res.ok) return showError('auth-error', data.error);
    startApp(data);
  } catch (e) {
    showError('auth-error', 'Connection error — is the server running?');
  }
}

async function register() {
  const username = document.getElementById('reg-username').value.trim();
  const email    = document.getElementById('reg-email').value.trim();
  const password = document.getElementById('reg-password').value.trim();
  if (!username || !email || !password) return showError('reg-error', 'Please fill all fields');

  try {
    const res = await fetch(`${API}/api/users/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    });
    const data = await res.json();
    if (!res.ok) return showError('reg-error', data.error);
    startApp(data);
  } catch (e) {
    showError('reg-error', 'Connection error — is the server running?');
  }
}

function startApp(user) {
  currentUser = user;
  document.getElementById('auth-screen').classList.add('hidden');
  document.getElementById('app').classList.remove('hidden');
  document.getElementById('header-username').textContent = user.username;
  updateHeaderBalance(user.cashBalance);
  showPage('market');
}

function logout() {
  currentUser = null;
  selectedStock = null;
  clearInterval(refreshTimer);
  document.getElementById('app').classList.add('hidden');
  document.getElementById('auth-screen').classList.remove('hidden');
}

// ===== NAVIGATION =====

function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active', 'hidden'));
  document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));

  document.querySelectorAll('.page').forEach(p => {
    if (p.id !== `page-${name}`) p.classList.add('hidden');
  });
  const page = document.getElementById(`page-${name}`);
  if (page) page.classList.add('active');

  const tabs = document.querySelectorAll('.nav-tab');
  const names = ['market', 'portfolio', 'history'];
  const idx = names.indexOf(name);
  if (idx >= 0 && tabs[idx]) tabs[idx].classList.add('active');

  clearInterval(refreshTimer);
  closeTradePanel();

  if (name === 'market') {
    loadMarket();
    refreshTimer = setInterval(loadMarket, 15000);
  } else if (name === 'portfolio') {
    loadPortfolio();
  } else if (name === 'history') {
    loadHistory();
  }
}

// ===== MARKET =====

async function loadMarket() {
  try {
    const res = await fetch(`${API}/api/stocks`);
    allStocks = await res.json();
    renderMarket(allStocks);
    if (selectedStock) {
      const refreshed = allStocks.find(s => s.symbol === selectedStock.symbol);
      if (refreshed) updateTradePanel(refreshed);
    }
  } catch (e) {
    document.getElementById('market-grid').innerHTML =
      '<div class="loading">⚠ Could not load market data</div>';
  }
}

function filterSector() {
  const sector = document.getElementById('sector-filter').value;
  const filtered = sector ? allStocks.filter(s => s.sector === sector) : allStocks;
  renderMarket(filtered);
}

function renderMarket(stocks) {
  const grid = document.getElementById('market-grid');
  if (!stocks.length) {
    grid.innerHTML = '<div class="loading">No stocks found</div>';
    return;
  }
  grid.innerHTML = stocks.map(s => `
    <div class="stock-card ${selectedStock?.symbol === s.symbol ? 'selected' : ''}"
         onclick="openTradePanel(${JSON.stringify(s).replace(/"/g, '&quot;')})">
      <div class="stock-header">
        <span class="stock-symbol">${s.symbol}</span>
        <span class="stock-sector">${s.sector}</span>
      </div>
      <p class="stock-company">${s.companyName}</p>
      <p class="stock-price">$${s.currentPrice.toFixed(2)}</p>
      <div class="stock-change">
        <span class="${s.changePercent >= 0 ? 'badge-up' : 'badge-down'}">
          ${s.changePercent >= 0 ? '+' : ''}${s.changePercent.toFixed(2)}%
        </span>
        <span style="font-size:12px; color: var(--muted);">
          ${s.changeAmount >= 0 ? '+' : ''}$${s.changeAmount.toFixed(2)}
        </span>
      </div>
    </div>
  `).join('');
}

// ===== TRADE PANEL =====

function openTradePanel(stock) {
  selectedStock = stock;
  updateTradePanel(stock);
  document.getElementById('trade-panel').classList.remove('hidden');
  document.getElementById('trade-qty').value = 1;
  updateCost();
  hideTradMsg();
}

function updateTradePanel(stock) {
  selectedStock = stock;
  document.getElementById('trade-symbol').textContent = stock.symbol;
  document.getElementById('trade-company').textContent = stock.companyName;
  document.getElementById('trade-price').textContent = `$${stock.currentPrice.toFixed(2)}`;
  const badge = document.getElementById('trade-change');
  const pct = stock.changePercent.toFixed(2);
  badge.textContent = `${pct >= 0 ? '+' : ''}${pct}%`;
  badge.className = stock.changePercent >= 0 ? 'badge-up' : 'badge-down';
  updateCost();
}

function closeTradePanel() {
  document.getElementById('trade-panel').classList.add('hidden');
  selectedStock = null;
  document.querySelectorAll('.stock-card').forEach(c => c.classList.remove('selected'));
}

function updateCost() {
  if (!selectedStock) return;
  const qty = parseInt(document.getElementById('trade-qty').value) || 0;
  const cost = (qty * selectedStock.currentPrice).toFixed(2);
  document.getElementById('trade-cost').textContent = `$${cost}`;
}

async function executeTrade(type) {
  if (!currentUser || !selectedStock) return;
  const qty = parseInt(document.getElementById('trade-qty').value);
  if (!qty || qty < 1) return showTradeMsg('Enter a valid quantity', 'error');

  try {
    const res = await fetch(`${API}/api/trade/${type}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: currentUser.id,
        symbol: selectedStock.symbol,
        quantity: qty
      })
    });
    const data = await res.json();
    if (!res.ok) return showTradeMsg(data.error, 'error');

    showTradeMsg(
      `${type.toUpperCase()} ${qty} × ${selectedStock.symbol} @ $${data.pricePerShare.toFixed(2)} = $${data.totalAmount.toFixed(2)}`,
      'success'
    );

    // Refresh user balance
    const userRes = await fetch(`${API}/api/users/${currentUser.id}`);
    const updatedUser = await userRes.json();
    currentUser.cashBalance = updatedUser.cashBalance;
    updateHeaderBalance(updatedUser.cashBalance);

    loadMarket();
  } catch (e) {
    showTradeMsg('Trade failed — server error', 'error');
  }
}

function showTradeMsg(msg, type) {
  const el = document.getElementById('trade-msg');
  el.textContent = msg;
  el.className = `trade-msg ${type}`;
  el.classList.remove('hidden');
}
function hideTradMsg() {
  document.getElementById('trade-msg').classList.add('hidden');
}

// ===== PORTFOLIO =====

async function loadPortfolio() {
  try {
    const [holdRes, sumRes] = await Promise.all([
      fetch(`${API}/api/portfolio/${currentUser.id}`),
      fetch(`${API}/api/portfolio/${currentUser.id}/summary`)
    ]);
    const holdings = await holdRes.json();
    const summary  = await sumRes.json();

    // Update summary cards
    document.getElementById('net-worth').textContent = `$${summary.netWorth.toFixed(2)}`;
    document.getElementById('cash-balance').textContent = `$${summary.cashBalance.toFixed(2)}`;
    document.getElementById('total-invested').textContent = `$${summary.totalInvested.toFixed(2)}`;
    document.getElementById('current-value').textContent = `$${summary.currentValue.toFixed(2)}`;
    const pnl = summary.totalPnl;
    const pnlEl = document.getElementById('total-pnl');
    pnlEl.textContent = `${pnl >= 0 ? '+' : ''}$${pnl.toFixed(2)} (${summary.pnlPercent.toFixed(2)}%)`;
    pnlEl.className = `card-value ${pnl >= 0 ? 'up' : 'down'}`;

    updateHeaderBalance(summary.cashBalance);

    // Render holdings table
    const tbody = document.getElementById('holdings-tbody');
    if (!holdings.length) {
      tbody.innerHTML = '<tr><td colspan="9" class="loading">No holdings yet — buy some stocks!</td></tr>';
      return;
    }
    tbody.innerHTML = holdings.map(h => {
      const pl = h.profitLoss;
      const plCls = pl >= 0 ? 'up' : 'down';
      return `
        <tr>
          <td class="symbol-cell">${h.stock.symbol}</td>
          <td>${h.stock.companyName}</td>
          <td class="mono">${h.quantity}</td>
          <td class="mono">$${h.avgBuyPrice.toFixed(2)}</td>
          <td class="mono">$${h.stock.currentPrice.toFixed(2)}</td>
          <td class="mono">$${h.totalCost.toFixed(2)}</td>
          <td class="mono">$${h.currentValue.toFixed(2)}</td>
          <td class="mono ${plCls}">${pl >= 0 ? '+' : ''}$${pl.toFixed(2)}</td>
          <td class="mono ${plCls}">${h.profitLossPercent >= 0 ? '+' : ''}${h.profitLossPercent.toFixed(2)}%</td>
        </tr>`;
    }).join('');
  } catch (e) {
    document.getElementById('holdings-tbody').innerHTML =
      '<tr><td colspan="9" class="loading">⚠ Could not load portfolio</td></tr>';
  }
}

// ===== HISTORY =====

async function loadHistory() {
  try {
    const res = await fetch(`${API}/api/portfolio/${currentUser.id}/transactions`);
    const txns = await res.json();
    const tbody = document.getElementById('history-tbody');
    if (!txns.length) {
      tbody.innerHTML = '<tr><td colspan="7" class="loading">No transactions yet</td></tr>';
      return;
    }
    tbody.innerHTML = txns.map(t => `
      <tr>
        <td class="mono">${formatDate(t.timestamp)}</td>
        <td><span class="${t.type === 'BUY' ? 'badge-buy' : 'badge-sell'}">${t.type}</span></td>
        <td class="symbol-cell">${t.stock.symbol}</td>
        <td>${t.stock.companyName}</td>
        <td class="mono">${t.quantity}</td>
        <td class="mono">$${t.pricePerShare.toFixed(2)}</td>
        <td class="mono ${t.type === 'BUY' ? 'down' : 'up'}">
          ${t.type === 'BUY' ? '-' : '+'}$${t.totalAmount.toFixed(2)}
        </td>
      </tr>
    `).join('');
  } catch (e) {
    document.getElementById('history-tbody').innerHTML =
      '<tr><td colspan="7" class="loading">⚠ Could not load history</td></tr>';
  }
}

// ===== HELPERS =====

function updateHeaderBalance(bal) {
  document.getElementById('header-balance').textContent = `$${bal.toFixed(2)}`;
}

function formatDate(ts) {
  const d = new Date(ts);
  return d.toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}

function showError(id, msg) {
  const el = document.getElementById(id);
  el.textContent = msg;
  el.classList.remove('hidden');
}

function clearError() {
  ['auth-error', 'reg-error'].forEach(id => {
    const el = document.getElementById(id);
    if (el) { el.textContent = ''; el.classList.add('hidden'); }
  });
}
