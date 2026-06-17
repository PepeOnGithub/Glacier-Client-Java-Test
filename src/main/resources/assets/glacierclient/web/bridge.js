/* Glacier web UI <-> Java bridge. Every [data-act] element fires its action.
   In-game a Java-side handler receives the action; in a plain browser it logs (for preview). */
(function () {
  function send(action) {
    // Primary in-game path: queue the action; the Java host drains window.__glq each frame.
    try { (window.__glq = window.__glq || []).push(action); } catch (e) {}
    try { if (window.glacierBridge && window.glacierBridge.send) { window.glacierBridge.send(action); return; } } catch (e) {}
    try { if (window.javaBridge && window.javaBridge.onAction) { window.javaBridge.onAction(action); return; } } catch (e) {}
    try { if (window.cefQuery) { window.cefQuery({ request: 'glacier:' + action, onSuccess: function () {}, onFailure: function () {} }); return; } } catch (e) {}
    console.log('[glacier] action:', action);
    var hint = document.getElementById('act-hint');
    if (!hint) { hint = document.createElement('div'); hint.id = 'act-hint';
      hint.style.cssText = 'position:fixed;left:50%;bottom:16px;transform:translateX(-50%);background:rgba(0,0,0,.7);color:#8ea9ff;padding:6px 14px;border-radius:10px;font:14px Inter,sans-serif;z-index:99;transition:opacity .3s';
      document.body.appendChild(hint); }
    hint.textContent = '→ ' + action; hint.style.opacity = '1';
    clearTimeout(window.__ah); window.__ah = setTimeout(function () { hint.style.opacity = '0'; }, 900);
  }

  function bind() {
    document.querySelectorAll('[data-act],[data-nav]').forEach(function (el) {
      if (el.__bound) return; el.__bound = true;
      el.addEventListener('click', function () {
        // el.animate (Web Animations API) is unavailable in Ultralight's WebKit build — guard it so a
        // missing API can never abort the handler before the action is sent.
        try { if (el.animate) el.animate([{ transform: 'scale(.94)' }, { transform: 'scale(1)' }],
          { duration: 150, easing: 'cubic-bezier(.22,1,.36,1)' }); } catch (e) {}
        var nav = el.getAttribute('data-nav');
        if (nav) { window.location.href = nav; return; }   // client-side screen navigation
        send(el.getAttribute('data-act'));
      });
    });
  }

  /** Java can call window.glacierSet('user','Name') etc. to push live data into the UI. */
  window.glacierSet = function (id, value) { var e = document.getElementById(id); if (e) e.textContent = value; };
  window.glacier = { send: send, bind: bind };
  if (document.readyState !== 'loading') bind(); else document.addEventListener('DOMContentLoaded', bind);
})();
