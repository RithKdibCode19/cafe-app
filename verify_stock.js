const axios = require('axios');

const API_BASE = 'http://localhost:8081/api';

async function verifyBranchStock() {
  try {
    // 1. Login
    console.log('Attempting login...');
    let token;
    try {
        const loginRes = await axios.post(`${API_BASE}/auth/login`, {
            username: 'admin',
            password: 'password'
        });
        token = loginRes.data.accessToken || loginRes.data.token;
    } catch (e) {
        console.log('Login with "password" failed, trying "1234"...');
        try {
            const loginRes = await axios.post(`${API_BASE}/auth/login`, {
                username: 'admin',
                password: '1234'
            });
            token = loginRes.data.accessToken || loginRes.data.token;
        } catch (e2) {
            console.error('Failed to login with both credentials');
            return;
        }
    }
    
    if (!token) {
        console.error('Failed to get token');
        return;
    }
    console.log('Login successful');

    // 2. Get Branches
    console.log('Fetching branches...');
    const branchesRes = await axios.get(`${API_BASE}/branches`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    
    const branchesData = branchesRes.data;
    const branches = Array.isArray(branchesData) ? branchesData : (branchesData.content || []);
    console.log(`Found ${branches.length} branches`);

    // 3. Check Stock for each branch
    for (const branch of branches.slice(0, 5)) {
        const branchId = branch.branchId || branch.id;
        const branchName = branch.name || branch.branchName;
        
        try {
            console.log(`\nChecking Branch: ${branchName} (ID: ${branchId})`);
            const stockRes = await axios.get(`${API_BASE}/inventory/branch/${branchId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            const stockData = stockRes.data;
            const stockList = Array.isArray(stockData) ? stockData : (stockData.content || []);
            
            console.log(`Total stock entries: ${stockList.length}`);
            
            if (stockList.length > 0) {
                console.log('Sample Stock Levels (first 3):');
                stockList.slice(0, 3).forEach(item => {
                    console.log(`- ${item.ingredientName || item.name}: ${item.currentStock} (Stock ID: ${item.branchStockId || item.id})`);
                });
            } else {
                console.log('No stock entries found.');
            }
        } catch (err) {
            console.error(`Failed to fetch stock for branch ${branchName}: ${err.message}`);
        }
    }

  } catch (error) {
    if (error.response) {
        console.error('Global Error:', error.response.status, error.response.data);
    } else {
        console.error('Global Error:', error.message);
    }
  }
}

verifyBranchStock();
