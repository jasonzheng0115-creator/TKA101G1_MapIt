/*!
* Start Bootstrap - Shop Homepage v5.0.6 (https://startbootstrap.com/template/shop-homepage)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-shop-homepage/blob/master/LICENSE)
*/

/**
 * Google Maps 初始化函數
 * 此函數會在 Google Maps API 載入完成後自動被呼叫
 */
function initMap() {
    // 檢查頁面上是否有地圖容器
    const mapContainer = document.getElementById('map');
    
    if (!mapContainer) {
        console.log('地圖容器 (#map) 不存在於此頁面');
        return;
    }
    
    // 設定地圖的預設中心點（台灣中心）
    const taiwanCenter = { lat: 23.5, lng: 121.0 };
    
    // 建立地圖實例
    const map = new google.maps.Map(mapContainer, {
        zoom: 8,
        center: taiwanCenter,
        mapTypeId: 'roadmap'
    });
    
    // 如果頁面上有景點資料，將它們標記在地圖上
    // 這裡假設景點資料會透過 data attributes 或全域變數傳遞
    if (window.attractionData && Array.isArray(window.attractionData)) {
        window.attractionData.forEach(function(attraction) {
            if (attraction.lat && attraction.lng) {
                // 建立標記
                const marker = new google.maps.Marker({
                    position: { lat: parseFloat(attraction.lat), lng: parseFloat(attraction.lng) },
                    map: map,
                    title: attraction.name || '景點'
                });
                
                // 建立資訊視窗
                const infoWindow = new google.maps.InfoWindow({
                    content: `
                        <div style="padding: 10px;">
                            <h5>${attraction.name || '景點'}</h5>
                            ${attraction.description ? `<p>${attraction.description}</p>` : ''}
                            ${attraction.url ? `<a href="${attraction.url}" target="_blank">查看詳情</a>` : ''}
                        </div>
                    `
                });
                
                // 點擊標記時顯示資訊視窗
                marker.addListener('click', function() {
                    infoWindow.open(map, marker);
                });
            }
        });
    }
    
    console.log('Google Maps 初始化完成');
}

/**
 * 當 Google Maps API 載入失敗時的處理
 */
window.gm_authFailure = function() {
    console.error('Google Maps API 驗證失敗，請檢查 API Key 是否正確');
    const mapContainer = document.getElementById('map');
    if (mapContainer) {
        mapContainer.innerHTML = '<div style="padding: 20px; text-align: center; color: red;">地圖載入失敗，請檢查 API Key 設定</div>';
    }
};

// 頁面載入完成後的初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log('MapIt 頁面已載入');
    
    // 這裡可以加入其他頁面初始化邏輯
    // 例如：圖片懶加載、動畫效果等
});

/**
 * 加入收藏功能
 * @param {number} attrId - 景點 ID
 */
function addToFavorite(attrId) {
    // 驗證景點 ID
    if (!attrId) {
        alert('景點 ID 無效');
        return;
    }
    
    // 使用 Fetch API 發送 POST 請求
    fetch('/front/attr/collect', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'attrId=' + attrId
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            // 可以在這裡更新收藏按鈕的狀態或樣式
            // 例如：將按鈕文字改為「已收藏」，或改變按鈕顏色
        } else {
            alert('收藏失敗：' + data.message);
        }
    })
    .catch(error => {
        console.error('收藏請求失敗:', error);
        alert('收藏功能暫時無法使用，請稍後再試');
    });
}
