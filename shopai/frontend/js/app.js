// ============================================================
// ShopAI India — Main App JavaScript
// ============================================================

// ── Cart State ──────────────────────────────────────────────
let cart     = JSON.parse(localStorage.getItem('shopai_cart')    || '[]');
let wishlist = JSON.parse(localStorage.getItem('shopai_wishlist')|| '[]');

function saveCart()     { localStorage.setItem('shopai_cart',    JSON.stringify(cart));     updateCartUI(); renderCartDrawer(); }
function saveWishlist() { localStorage.setItem('shopai_wishlist', JSON.stringify(wishlist)); updateWishlistUI(); }

function updateCartUI() {
  const n = cart.reduce((a,b)=>a+b.qty, 0);
  document.querySelectorAll('#cartCount,#cdCount').forEach(el=>el.textContent=n);
}
function updateWishlistUI() {
  document.querySelectorAll('#wishCount').forEach(el=>el.textContent=wishlist.length);
}

// ── Helpers ──────────────────────────────────────────────────
const fmt = n => new Intl.NumberFormat('en-IN').format(Math.round(n));
const finalPrice = p => p.discount > 0
  ? Math.round(p.price - p.price * p.discount / 100)
  : p.price;
const stars = r => {
  const f=Math.floor(r), h=r%1>=.5?1:0, e=5-f-h;
  return '★'.repeat(f)+(h?'½':'')+'☆'.repeat(e);
};

// ── Cart Actions ─────────────────────────────────────────────
function addToCart(id) {
  const p = PRODUCTS.find(x=>x.id===id);
  if (!p) return;
  const ex = cart.find(x=>x.id===id);
  if (ex) ex.qty++;
  else cart.push({id:p.id, name:p.name, brand:p.brand, img:p.img, price:p.price, finalPrice:finalPrice(p), qty:1});
  saveCart();
  showToast('✅ '+p.name+' added to cart!');
}
function removeFromCart(id) {
  cart = cart.filter(x=>x.id!==id);
  saveCart();
}
function toggleWishlist(id) {
  const idx = wishlist.indexOf(id);
  if (idx>-1) { wishlist.splice(idx,1); showToast('Removed from wishlist'); }
  else         { wishlist.push(id);      showToast('❤️ Added to wishlist!'); }
  saveWishlist();
  document.querySelectorAll('[data-wl="'+id+'"]').forEach(b=>b.classList.toggle('active',wishlist.includes(id)));
}

// ── Cart Drawer ───────────────────────────────────────────────
function openCartDrawer()  { document.getElementById('cartDrawer')?.classList.add('open'); document.getElementById('cartOverlay')?.classList.add('show'); }
function closeCartDrawer() { document.getElementById('cartDrawer')?.classList.remove('open'); document.getElementById('cartOverlay')?.classList.remove('show'); }

function renderCartDrawer() {
  const el = document.getElementById('cdItems');
  const tot = document.getElementById('cdTotal');
  if (!el) return;
  if (!cart.length) { el.innerHTML='<div class="cd-empty"><i class="fas fa-shopping-cart"></i><p>Your cart is empty</p></div>'; if(tot)tot.textContent='₹0'; return; }
  el.innerHTML = cart.map(item=>`
    <div class="cd-item">
      <img src="${item.img}" alt="${item.name}" onerror="this.src='https://via.placeholder.com/56'"/>
      <div class="cdi-info"><div class="cdi-name">${item.name}</div><div class="cdi-price">₹${fmt(item.finalPrice)} × ${item.qty}</div></div>
      <button class="cdi-rm" onclick="removeFromCart(${item.id})"><i class="fas fa-times"></i></button>
    </div>`).join('');
  if (tot) tot.textContent='₹'+fmt(cart.reduce((s,i)=>s+i.finalPrice*i.qty,0));
}

// ── Product Card HTML ─────────────────────────────────────────
function renderProductCard(p) {
  const fp = finalPrice(p);
  const saved = p.price - fp;
  const wl = wishlist.includes(p.id);
  return `
  <div class="prod-card" onclick="viewProduct(${p.id})">
    <div class="pc-img-wrap">
      <img src="${p.img}" alt="${p.name}" class="pc-img" onerror="this.src='https://via.placeholder.com/300x300?text=No+Image'"/>
      <div class="pc-badges">
        ${p.discount>0?'<span class="pbadge pbadge-disc">'+p.discount+'% OFF</span>':''}
        ${p.trending?'<span class="pbadge pbadge-trend">🔥 Trending</span>':''}
        ${p.featured?'<span class="pbadge pbadge-ai">✨ AI Pick</span>':''}
      </div>
      <div class="pc-actions">
        <button class="pab ${wl?'active':''}" data-wl="${p.id}" onclick="event.stopPropagation();toggleWishlist(${p.id})" title="Wishlist"><i class="fas fa-heart"></i></button>
        <button class="pab" onclick="event.stopPropagation();addToCart(${p.id})" title="Add to Cart"><i class="fas fa-cart-plus"></i></button>
        <button class="pab" onclick="event.stopPropagation();viewProduct(${p.id})" title="View"><i class="fas fa-eye"></i></button>
      </div>
    </div>
    <div class="pc-info">
      <div class="pc-brand">${p.brand}</div>
      <div class="pc-name">${p.aiTitle||p.name}</div>
      <div class="pc-ai-desc">${p.aiDesc}</div>
      <div class="pc-rating"><span class="stars">${stars(p.rating)}</span><span class="rv">${p.rating}</span><span class="rc">(${fmt(p.reviews)})</span></div>
      <div class="pc-price">
        <span class="pf-price">₹${fmt(fp)}</span>
        ${p.discount>0?'<span class="po-price">₹'+fmt(p.price)+'</span><span class="ps-price">Save ₹'+fmt(saved)+'</span>':''}
      </div>
      <button class="btn-add-cart" onclick="event.stopPropagation();addToCart(${p.id})"><i class="fas fa-cart-plus"></i> Add to Cart</button>
    </div>
  </div>`;
}

function renderGrid(products, containerId) {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.innerHTML = products.length
    ? products.map(p=>renderProductCard(p)).join('')
    : '<div style="grid-column:1/-1;text-align:center;padding:60px 20px;color:#999"><i class="fas fa-box-open" style="font-size:48px;color:#ddd;display:block;margin-bottom:12px"></i><p>No products found</p></div>';
}

function viewProduct(id) { window.location.href='product.html?id='+id; }

// ── Categories ────────────────────────────────────────────────
function renderCategories() {
  const el = document.getElementById('catGrid');
  if (!el) return;
  el.innerHTML = CATEGORIES.map(c=>`
    <a href="products.html?cat=${c.slug}" class="cat-card">
      <div class="cat-icon"><i class="${c.icon}"></i></div>
      <div class="cat-name">${c.name}</div>
      <div class="cat-count">${c.count}</div>
    </a>`).join('');
}

// ── Trending Filter ────────────────────────────────────────────
function filterTrending(cat, btn) {
  document.querySelectorAll('.tab').forEach(t=>t.classList.remove('active'));
  btn.classList.add('active');
  const products = PRODUCTS.filter(p=>p.trending && (cat==='all'||p.category===cat));
  renderGrid(products, 'trendingGrid');
}

// ── Slider ───────────────────────────────────────────────────
let slide=0, total=2;
function updateSlider() {
  const t=document.getElementById('slides');
  if(t) t.style.transform=`translateX(-${slide*100}%)`;
  document.querySelectorAll('.sl-dot').forEach((d,i)=>d.classList.toggle('active',i===slide));
}
function nextSlide() { slide=(slide+1)%total; updateSlider(); }
function prevSlide() { slide=(slide-1+total)%total; updateSlider(); }

function initSlider() {
  const dots=document.getElementById('slDots');
  if(!dots) return;
  dots.innerHTML=Array.from({length:total},(_,i)=>`<div class="sl-dot ${i===0?'active':''}" onclick="slide=${i};updateSlider()"></div>`).join('');
  setInterval(nextSlide, 5000);
}

// ── Countdown ────────────────────────────────────────────────
function initCountdown() {
  let d1=new Date().getTime()+8*3600*1000;
  let d2=new Date().getTime()+8*3600*1000;
  function tick() {
    const n=new Date().getTime();
    const r1=d1-n, r2=d2-n;
    ['h','m','s'].forEach((u,i)=>{
      const ms=[3600000,60000,1000];
      const v=Math.floor((r1%(ms[i]*60||ms[i]*24))/ms[i]);
      ['ch','cm','cs'].forEach((id,j)=>{if(j===i){const e=document.getElementById(id);if(e)e.textContent=String(Math.max(0,v)).padStart(2,'0');}});
      const v2=Math.floor((r2%(ms[i]*60||ms[i]*24))/ms[i]);
      ['dt-h','dt-m','dt-s'].forEach((id,j)=>{if(j===i){const e=document.getElementById(id);if(e)e.textContent=String(Math.max(0,v2)).padStart(2,'0');}});
    });
  }
  tick(); setInterval(tick,1000);
}

// ── Search ───────────────────────────────────────────────────
function doSearch() {
  const q=document.getElementById('searchInput')?.value.trim();
  if(q) location.href='products.html?search='+encodeURIComponent(q);
}
function initSearch() {
  const inp=document.getElementById('searchInput'), sug=document.getElementById('searchSuggest');
  if(!inp||!sug) return;
  inp.addEventListener('input',function(){
    const v=this.value.trim().toLowerCase();
    if(v.length<2){sug.classList.remove('active');return;}
    const matches=PRODUCTS.filter(p=>p.name.toLowerCase().includes(v)||p.brand.toLowerCase().includes(v)).slice(0,5);
    if(matches.length){
      sug.innerHTML=matches.map(p=>`<div class="sug-item" onclick="location.href='products.html?search=${encodeURIComponent(p.name)}'"><i class="fas fa-tag"></i> ${p.name}</div>`).join('');
      sug.classList.add('active');
    } else sug.classList.remove('active');
  });
  inp.addEventListener('keypress',e=>{if(e.key==='Enter')doSearch();});
  document.addEventListener('click',e=>{if(!inp.contains(e.target))sug.classList.remove('active');});
}

// ── User Menu ─────────────────────────────────────────────────
function toggleUserMenu() { document.getElementById('userDropdown')?.classList.toggle('show'); }
document.addEventListener('click',e=>{const b=document.getElementById('userBtn');if(b&&!b.contains(e.target))document.getElementById('userDropdown')?.classList.remove('show');});
function logout(){ localStorage.removeItem('shopai_user'); showToast('Logged out'); setTimeout(()=>location.href='login.html',1000); }

// ── Newsletter ────────────────────────────────────────────────
function subscribeNL(e) {
  e.preventDefault();
  const em=e.target.querySelector('input[type=email]').value;
  showToast('✅ Subscribed! AI deals coming to '+em);
  e.target.reset();
}

// ── Toast ─────────────────────────────────────────────────────
function showToast(msg) {
  const t=document.getElementById('toast'), m=document.getElementById('toastMsg');
  if(!t) return;
  m.textContent=msg; t.classList.add('show');
  setTimeout(()=>t.classList.remove('show'),3000);
}

// ── Sticky Header ─────────────────────────────────────────────
function initStickyHeader(){
  window.addEventListener('scroll',()=>{
    const h=document.getElementById('mainHeader');
    if(h) h.style.boxShadow=window.scrollY>60?'0 4px 20px rgba(0,0,0,.15)':'0 2px 12px rgba(0,0,0,.1)';
  });
}

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded',()=>{
  renderCategories();
  renderGrid(PRODUCTS.filter(p=>p.trending).slice(0,8),'trendingGrid');
  renderGrid(PRODUCTS.filter(p=>p.featured).slice(0,8),'featuredGrid');
  renderGrid(PRODUCTS.filter(p=>p.discount>=10).sort((a,b)=>b.discount-a.discount).slice(0,8),'dealsGrid');
  initSlider();
  initCountdown();
  initSearch();
  initStickyHeader();
  updateCartUI();
  updateWishlistUI();
  renderCartDrawer();
});
