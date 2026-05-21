// ============================================================
// ShopAI India — Enhanced AI Chatbot v2 (Gemini Powered)
// ============================================================

let chatOpen = false;
const CB_HISTORY = [];

// ── Toggle ────────────────────────────────────────────────────
function toggleChatbot() {
  chatOpen = !chatOpen;
  const bot = document.getElementById('chatbot');
  bot?.classList.toggle('open', chatOpen);
  if (chatOpen) {
    document.getElementById('cbInput')?.focus();
    if (!document.getElementById('cbMsgs')?.children.length) showWelcome();
  }
}

function cbKeyPress(e) { if (e.key === 'Enter') sendChat(); }
function quickChat(msg) {
  const i = document.getElementById('cbInput');
  if (i) { i.value = msg; }
  sendChat();
}

// ── Welcome ───────────────────────────────────────────────────
function showWelcome() {
  const greetings = ['Good morning', 'Good afternoon', 'Good evening'];
  const h = new Date().getHours();
  const greet = h < 12 ? greetings[0] : h < 17 ? greetings[1] : greetings[2];
  appendMsg(
    `🙏 ${greet}! Welcome to <strong>ShopAI India</strong>!<br><br>
    I'm your personal shopping assistant. I can help you:<br>
    • 🔍 Find products & compare prices<br>
    • 🏷️ Get the best deals & offers<br>
    • 📦 Track orders & returns<br>
    • 🤖 Get personalized recommendations<br><br>
    <div class="cb-chips">
      <button onclick="quickChat('Show me trending products')">🔥 Trending</button>
      <button onclick="quickChat('Best deals today')">💰 Deals</button>
      <button onclick="quickChat('Electronics under 5000')">📱 Electronics</button>
      <button onclick="quickChat('Beauty products')">✨ Beauty</button>
    </div>`, 'bot');
}

// ── Send ──────────────────────────────────────────────────────
async function sendChat() {
  const inp = document.getElementById('cbInput');
  if (!inp) return;
  const msg = inp.value.trim();
  if (!msg) return;
  inp.value = '';
  appendMsg(msg, 'user');
  CB_HISTORY.push({ role: 'user', content: msg });

  const tid = 't' + Date.now();
  appendTyping(tid);

  // ── Call Gemini AI directly ──
  try {
    const geminiRes = await fetch(
      'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyCAqx79iQBN3kxvQs6lY5W8NdeJTEmuPwQ',
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{
            parts: [{
              text: `You are a helpful shopping assistant for ShopAI India, an Indian e-commerce platform.
Answer in 2-3 sentences using friendly Indian English.
Help with products, deals, orders, and recommendations.
Customer message: ${msg}`
            }]
          }]
        })
      }
    );
    removeTyping(tid);
    if (geminiRes.ok) {
      const geminiData = await geminiRes.json();
      const reply = geminiData.candidates?.[0]?.content?.parts?.[0]?.text || smartAI(msg);
      appendMsg(reply, 'bot');
      CB_HISTORY.push({ role: 'assistant', content: reply });
      return;
    }
  } catch (_) { /* fallback to smartAI */ }

  removeTyping(tid);
  const reply = smartAI(msg);
  appendMsg(reply, 'bot');
  CB_HISTORY.push({ role: 'assistant', content: reply });
}

// ── Smart Local AI (fallback if Gemini fails) ─────────────────
function smartAI(msg) {
  const m = msg.toLowerCase();

  // ── Greetings ──
  if (/^(hi|hello|hey|namaste|hola|namaskar|jai hind|good\s*(morning|evening|afternoon))/.test(m)) {
    const h = new Date().getHours();
    const t = h < 12 ? 'morning' : h < 17 ? 'afternoon' : 'evening';
    return `🙏 Good ${t}! How can I help you shop today?<br><br>
      <div class="cb-chips">
        <button onclick="quickChat('Show trending products')">🔥 Trending</button>
        <button onclick="quickChat('Best deals today')">💰 Today Deals</button>
        <button onclick="quickChat('New arrivals')">✨ New Arrivals</button>
      </div>`;
  }

  // ── Price budget search ──
  const budgetMatch = m.match(/under\s*[₹rs\s]*(\d[\d,]*)/i) || m.match(/below\s*[₹rs\s]*(\d[\d,]*)/i) || m.match(/less than\s*[₹rs\s]*(\d[\d,]*)/i) || m.match(/(\d[\d,]*)\s*se\s*kam/i);
  if (budgetMatch) {
    const budget = parseInt(budgetMatch[1].replace(/,/g, ''));
    let cat = null;
    for (const c of CATEGORIES) {
      if (m.includes(c.slug) || m.includes(c.name.toLowerCase())) { cat = c.slug; break; }
    }
    const found = PRODUCTS.filter(p => {
      const fp = p.discount > 0 ? Math.round(p.price - p.price * p.discount / 100) : p.price;
      return fp <= budget && (!cat || p.category === cat);
    }).sort((a, b) => b.rating - a.rating).slice(0, 4);
    if (found.length) return productList(found, `Top picks under ₹${budget.toLocaleString('en-IN')}:`);
    return `😔 Sorry, no products found under ₹${budget.toLocaleString('en-IN')}${cat ? ' in ' + cat : ''}. Try a higher budget or <a href="products.html">browse all</a>.`;
  }

  // ── Category search ──
  for (const c of CATEGORIES) {
    if (m.includes(c.slug) || m.includes(c.name.toLowerCase())) {
      const items = PRODUCTS.filter(p => p.category === c.slug).sort((a, b) => b.rating - a.rating).slice(0, 4);
      return productList(items, `🛍️ Top ${c.name} picks for you:`);
    }
  }

  // ── Product name search ──
  const nameResults = PRODUCTS.filter(p =>
    p.name.toLowerCase().split(' ').some(w => w.length > 3 && m.includes(w)) ||
    p.brand.toLowerCase().split(' ').some(w => w.length > 2 && m.includes(w))
  ).slice(0, 4);
  if (nameResults.length) return productList(nameResults, '🔍 Here\'s what I found:');

  // ── Trending ──
  if (/trend|popular|best seller|top|hot/.test(m)) {
    const items = PRODUCTS.filter(p => p.trending).sort(() => Math.random() - 0.5).slice(0, 4);
    return productList(items, '🔥 Currently trending on ShopAI India:');
  }

  // ── Deals & Discounts ──
  if (/deal|discount|offer|sale|coupon|promo|code|cheap|save/.test(m)) {
    return `💰 <strong>Today's Best Deals:</strong><br><br>
    🏷️ Promo Codes:<br>
    • <strong>AISHIP50</strong> – 50% OFF (min ₹999)<br>
    • <strong>INDIA200</strong> – ₹200 OFF on ₹999+<br>
    • <strong>NEWUSER30</strong> – 30% OFF (new users)<br>
    • <strong>MONSOON25</strong> – 25% OFF on Fashion<br><br>
    <div class="cb-chips">
      <button onclick="quickChat('Show discounted electronics')">📱 Electronics Sale</button>
      <button onclick="quickChat('Fashion deals')">👗 Fashion Sale</button>
    </div>`;
  }

  // ── Orders & Tracking ──
  if (/order|track|delivery|ship|dispatch|status/.test(m)) {
    return `📦 <strong>Order Tracking & Help:</strong><br><br>
    To track your order:<br>
    1. Go to <strong>My Account → My Orders</strong><br>
    2. Click on your Order ID<br>
    3. See real-time tracking<br><br>
    📞 Need help? Call <strong>+91 98765 43210</strong><br>
    📧 Email: <strong>support@shopai.in</strong><br>
    💬 Chat support: <strong>9AM – 9PM IST</strong>`;
  }

  // ── Returns & Refunds ──
  if (/return|refund|replace|exchange|cancel/.test(m)) {
    return `🔄 <strong>ShopAI Returns Policy:</strong><br><br>
    ✅ 30-day hassle-free returns<br>
    ✅ Full refund in 5–7 business days<br>
    ✅ Free pickup for defective items<br>
    ✅ No questions asked for 7 days<br><br>
    To start a return:<br>
    <strong>My Account → My Orders → Return Item</strong>`;
  }

  // ── Payments ──
  if (/pay|payment|upi|card|emi|cod|netbank|wallet/.test(m)) {
    return `💳 <strong>Payment Options:</strong><br><br>
    • 📱 UPI – GPay, PhonePe, Paytm, BHIM<br>
    • 💳 Credit / Debit Cards (Visa, Mastercard, RuPay)<br>
    • 🌐 Net Banking (all major banks)<br>
    • 💵 Cash on Delivery (₹50 charge)<br>
    • 📅 EMI from ₹3,000+ (0% on select cards)<br>
    • 👛 Wallets – Paytm, Amazon Pay<br><br>
    🔒 All payments are 100% secure & encrypted!`;
  }

  // ── Recommendations ──
  if (/recommend|suggest|best for me|what to buy|help me choose/.test(m)) {
    const featured = PRODUCTS.filter(p => p.featured).sort(() => Math.random() - 0.5).slice(0, 4);
    return productList(featured, '✨ AI-curated picks just for you:');
  }

  // ── New arrivals ──
  if (/new|latest|fresh|arrival|just (in|added|launched)/.test(m)) {
    const items = PRODUCTS.slice(-12).reverse().slice(0, 4);
    return productList(items, '🆕 Latest arrivals on ShopAI India:');
  }

  // ── Gifting ──
  if (/gift|present|birthday|anniversary|diwali|holi|christmas|wedding/.test(m)) {
    const items = PRODUCTS.filter(p => p.featured || p.rating >= 4.6).sort(() => Math.random() - 0.5).slice(0, 4);
    return productList(items, '🎁 Top-rated gift ideas:');
  }

  // ── Price comparison ──
  if (/compar|vs|versus|better|cheaper|difference between/.test(m)) {
    return `🤖 I can compare products for you! Just tell me which two products you want to compare, for example:<br><br>
    <em>"Compare boAt Airdopes vs Sony headphones"</em><br><br>
    Or browse our <a href="products.html">product catalogue</a> with filter & sort options for easy comparison!`;
  }

  // ── About / Help ──
  if (/about|who are you|what is shopai|help|what can you do/.test(m)) {
    return `🤖 I'm <strong>ShopAI Assistant</strong> – your personal AI-powered shopping guide!<br><br>
    I can help you with:<br>
    • 🔍 Search 80+ Indian products<br>
    • 💰 Find best deals & promo codes<br>
    • 📦 Track orders & start returns<br>
    • 🎁 Gift recommendations<br>
    • 💳 Payment queries<br><br>
    Just type what you're looking for in plain English or Hindi!`;
  }

  // ── Default fallback with suggestions ──
  const fallbacks = [
    `🤔 I didn't quite catch that! Try asking me:<br>
    <div class="cb-chips">
      <button onclick="quickChat('Best electronics under 10000')">📱 Electronics</button>
      <button onclick="quickChat('Show fashion deals')">👗 Fashion</button>
      <button onclick="quickChat('Trending products')">🔥 Trending</button>
    </div>`,
    `🛍️ Let me help you find the perfect product! You can ask about:<br>
    <div class="cb-chips">
      <button onclick="quickChat('Books under 500')">📚 Books</button>
      <button onclick="quickChat('Beauty products')">💄 Beauty</button>
      <button onclick="quickChat('Sports equipment')">🏋️ Sports</button>
    </div>`,
    `🙏 Sorry, I couldn't understand that. Try something like:<br>
    <em>"Show me groceries under ₹200"</em> or <em>"Best laptop under ₹50000"</em><br><br>
    Or visit our <a href="products.html">products page</a> to browse everything!`
  ];
  return fallbacks[Math.floor(Math.random() * fallbacks.length)];
}

// ── Product list renderer ─────────────────────────────────────
function productList(products, title) {
  if (!products.length) return '😔 No products found. Try a different search!';
  const fmt = n => new Intl.NumberFormat('en-IN').format(Math.round(n));
  const cards = products.map(p => {
    const fp = p.discount > 0 ? Math.round(p.price - p.price * p.discount / 100) : p.price;
    return `<div class="cb-prod-card" onclick="viewProduct(${p.id})">
      <img src="${p.img}" alt="${p.name}" onerror="this.src='https://via.placeholder.com/50'">
      <div class="cb-prod-info">
        <div class="cb-prod-name">${p.name}</div>
        <div class="cb-prod-price">₹${fmt(fp)}${p.discount > 0 ? ` <span class="cb-disc">${p.discount}% OFF</span>` : ''}</div>
        <div class="cb-prod-rating">⭐ ${p.rating}</div>
      </div>
      <button class="cb-add-btn" onclick="event.stopPropagation();addToCart(${p.id})">Add</button>
    </div>`;
  }).join('');
  return `${title}<br><div class="cb-products">${cards}</div>
    <div class="cb-chips" style="margin-top:8px">
      <button onclick="location.href='products.html'">View All Products →</button>
    </div>`;
}

// ── DOM helpers ───────────────────────────────────────────────
function appendMsg(text, role) {
  const c = document.getElementById('cbMsgs');
  if (!c) return;
  const d = document.createElement('div');
  d.className = 'cb-msg ' + (role === 'user' ? 'user' : 'bot');
  d.innerHTML = `<div class="cb-bubble">${text}</div>`;
  c.appendChild(d);
  c.scrollTop = c.scrollHeight;
}

function appendTyping(id) {
  const c = document.getElementById('cbMsgs');
  if (!c) return;
  const d = document.createElement('div');
  d.id = id; d.className = 'cb-msg bot';
  d.innerHTML = `<div class="cb-bubble" style="display:flex;gap:4px;padding:12px 14px">
    <span style="width:7px;height:7px;background:var(--primary);border-radius:50%;animation:typing 1s infinite 0s"></span>
    <span style="width:7px;height:7px;background:var(--primary);border-radius:50%;animation:typing 1s infinite .2s"></span>
    <span style="width:7px;height:7px;background:var(--primary);border-radius:50%;animation:typing 1s infinite .4s"></span>
  </div>`;
  c.appendChild(d); c.scrollTop = c.scrollHeight;
  if (!document.getElementById('cbStyle')) {
    const s = document.createElement('style'); s.id = 'cbStyle';
    s.textContent = `@keyframes typing{0%,100%{opacity:.3;transform:scale(.8)}50%{opacity:1;transform:scale(1.2)}}
    .cb-prod-card{display:flex;align-items:center;gap:8px;padding:8px;border:1px solid #eee;border-radius:8px;margin:4px 0;cursor:pointer;background:#fff;transition:box-shadow .2s}
    .cb-prod-card:hover{box-shadow:0 2px 8px rgba(0,0,0,.15)}
    .cb-prod-card img{width:48px;height:48px;border-radius:6px;object-fit:cover;flex-shrink:0}
    .cb-prod-info{flex:1;min-width:0}
    .cb-prod-name{font-size:11px;font-weight:600;color:#1a1a2e;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
    .cb-prod-price{font-size:12px;font-weight:700;color:#e94560}
    .cb-prod-rating{font-size:10px;color:#888}
    .cb-disc{background:#e94560;color:#fff;padding:1px 4px;border-radius:3px;font-size:9px;margin-left:4px}
    .cb-add-btn{background:var(--primary,#e94560);color:#fff;border:none;border-radius:6px;padding:4px 8px;font-size:10px;cursor:pointer;white-space:nowrap;flex-shrink:0}
    .cb-add-btn:hover{opacity:.85}
    .cb-products{display:flex;flex-direction:column;gap:4px;margin-top:8px}
    .cb-chips{display:flex;flex-wrap:wrap;gap:6px;margin-top:8px}
    .cb-chips button{background:#f0f4ff;color:#4361ee;border:1px solid #c5d2ff;border-radius:16px;padding:4px 10px;font-size:11px;cursor:pointer;transition:all .2s}
    .cb-chips button:hover{background:#4361ee;color:#fff}
    .cb-bubble a{color:#4361ee;text-decoration:underline}`;
    document.head.appendChild(s);
  }
}

function removeTyping(id) { document.getElementById(id)?.remove(); }

// ── Clear chat ────────────────────────────────────────────────
function clearChat() {
  const c = document.getElementById('cbMsgs');
  if (c) c.innerHTML = '';
  CB_HISTORY.length = 0;
  showWelcome();
}
