<template>
  <div class="receipt-container">
    <!-- Receipt Content -->
    <div ref="receiptRef" class="receipt">
      <!-- Header -->
      <div class="receipt-header">
        <div class="logo">☕</div>
        <h1 class="store-name">{{ receipt?.branchName || 'Cafe POS' }}</h1>
        <p class="store-info" v-if="receipt?.branchPhone">{{ receipt.branchPhone }}</p>
        <p class="store-info" v-if="receipt?.branchAddress">{{ receipt.branchAddress }}</p>
      </div>
      
      <div class="divider dashed"></div>
      
      <!-- Order Info -->
      <div class="order-info">
        <div class="info-row">
          <span>Order #:</span>
          <span class="bold">{{ receipt?.orderNo }}</span>
        </div>
        <div class="info-row">
          <span>Date:</span>
          <span>{{ receipt?.orderDate }}</span>
        </div>
        <div class="info-row">
          <span>Time:</span>
          <span>{{ receipt?.orderTime }}</span>
        </div>
        <div class="info-row">
          <span>Type:</span>
          <span>{{ receipt?.orderType }}</span>
        </div>
        <div class="info-row">
          <span>Cashier:</span>
          <span>{{ receipt?.cashierName }}</span>
        </div>
        <div class="info-row" v-if="receipt?.customerName">
          <span>Customer:</span>
          <span>{{ receipt?.customerName }}</span>
        </div>
      </div>
      
      <div class="divider solid"></div>
      
      <!-- Items -->
      <div class="items-section">
        <div class="items-header">
          <span class="col-item">Item</span>
          <span class="col-qty">Qty</span>
          <span class="col-price">Price</span>
          <span class="col-total">Total</span>
        </div>
        <div class="divider thin"></div>
        
        <div v-for="(item, idx) in receipt?.items" :key="idx" class="item-row">
          <div class="item-name-row">
            <span class="item-name">{{ item.name }}</span>
            <span class="item-variant" v-if="item.variant">({{ item.variant }})</span>
          </div>
          <div class="item-details-row">
            <span class="col-qty">{{ item.qty }}</span>
            <span class="col-price">${{ item.unitPrice?.toFixed(2) }}</span>
            <span class="col-total">${{ item.lineTotal?.toFixed(2) }}</span>
          </div>
          <div class="item-addons" v-if="item.addons">
            + {{ item.addons }}
          </div>
        </div>
      </div>
      
      <div class="divider solid"></div>
      
      <!-- Totals -->
      <div class="totals-section">
        <div class="total-row">
          <span>Subtotal:</span>
          <span>${{ receipt?.subTotal?.toFixed(2) }}</span>
        </div>
        <div class="total-row" v-if="receipt?.discountAmount > 0">
          <span>Discount:</span>
          <span class="discount">-${{ receipt?.discountAmount?.toFixed(2) }}</span>
        </div>
        <div class="total-row" v-if="receipt?.taxAmount > 0">
          <span>Tax:</span>
          <span>${{ receipt?.taxAmount?.toFixed(2) }}</span>
        </div>
        <div class="divider thin"></div>
        <div class="total-row grand-total">
          <span>TOTAL:</span>
          <span>${{ receipt?.totalAmount?.toFixed(2) }}</span>
        </div>
      </div>
      
      <div class="divider dashed"></div>
      
      <!-- Payment Info -->
      <div class="payment-section">
        <div class="payment-row">
          <span>Payment Method:</span>
          <span class="bold">{{ receipt?.paymentMethod }}</span>
        </div>
        <div class="payment-row">
          <span>Amount Paid:</span>
          <span>${{ receipt?.amountPaid?.toFixed(2) }}</span>
        </div>
        <div class="payment-row" v-if="receipt?.changeAmount > 0">
          <span>Change:</span>
          <span class="change">${{ receipt?.changeAmount?.toFixed(2) }}</span>
        </div>
      </div>
      
      <div class="divider dashed"></div>
      
      <!-- Footer -->
      <div class="receipt-footer">
        <p class="thank-you">{{ receipt?.footerMessage }}</p>
        <div class="qr-placeholder">
          <span>📱</span>
        </div>
        <p class="powered-by">Powered by Cafe POS</p>
      </div>
    </div>
    
    <!-- Actions -->
    <div class="receipt-actions" v-if="showActions">
      <button @click="printReceipt" class="btn btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="6 9 6 2 18 2 18 9"></polyline>
          <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
          <rect x="6" y="14" width="12" height="8"></rect>
        </svg>
        Print Receipt
      </button>
      <button @click="$emit('close')" class="btn btn-secondary">
        Close
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface ReceiptItem {
  name: string
  variant?: string
  qty: number
  unitPrice: number
  lineTotal: number
  addons?: string
}

interface Receipt {
  orderNo: string
  branchName: string
  branchPhone?: string
  branchAddress?: string
  cashierName: string
  customerName?: string
  orderType: string
  orderDate: string
  orderTime: string
  items: ReceiptItem[]
  subTotal: number
  discountAmount: number
  taxAmount: number
  totalAmount: number
  paymentMethod: string
  amountPaid: number
  changeAmount: number
  footerMessage: string
}

const props = defineProps<{
  orderId?: number
  orderNo?: string
  showActions?: boolean
}>()

const emit = defineEmits(['close'])

const { get } = useApi()
const receipt = ref<Receipt | null>(null)
const receiptRef = ref<HTMLElement | null>(null)
const loading = ref(false)

const fetchReceipt = async () => {
  loading.value = true
  try {
    let response
    if (props.orderId) {
      response = await get<{ success: boolean, data: Receipt }>(`/receipts/${props.orderId}`)
    } else if (props.orderNo) {
      response = await get<{ success: boolean, data: Receipt }>(`/receipts/by-order-no?orderNo=${props.orderNo}`)
    }
    if (response?.data) {
      receipt.value = response.data
    }
  } catch (err) {
    console.error('Failed to fetch receipt:', err)
  } finally {
    loading.value = false
  }
}

const printReceipt = () => {
  const printWindow = window.open('', '_blank')
  if (printWindow && receiptRef.value) {
    printWindow.document.write(`
      <html>
        <head>
          <title>Receipt - ${receipt.value?.orderNo}</title>
          <style>
            body { font-family: 'Courier New', monospace; padding: 20px; max-width: 300px; margin: 0 auto; }
            .receipt { background: white; padding: 20px; }
            .receipt-header { text-align: center; margin-bottom: 15px; }
            .logo { font-size: 40px; }
            .store-name { font-size: 18px; font-weight: bold; margin: 10px 0 5px; }
            .store-info { font-size: 12px; color: #666; margin: 2px 0; }
            .divider { border-bottom: 1px dashed #ccc; margin: 10px 0; }
            .divider.solid { border-bottom-style: solid; }
            .divider.thin { margin: 5px 0; }
            .info-row, .total-row, .payment-row { display: flex; justify-content: space-between; font-size: 12px; margin: 3px 0; }
            .bold { font-weight: bold; }
            .items-header { display: flex; font-size: 11px; font-weight: bold; }
            .col-item { flex: 2; }
            .col-qty, .col-price, .col-total { flex: 1; text-align: right; }
            .item-row { margin: 8px 0; }
            .item-name-row { font-size: 12px; }
            .item-variant { font-size: 10px; color: #666; }
            .item-details-row { display: flex; font-size: 11px; }
            .item-addons { font-size: 10px; color: #888; margin-left: 10px; }
            .grand-total { font-size: 14px; font-weight: bold; margin-top: 5px; }
            .discount { color: #d63031; }
            .change { color: #00b894; font-weight: bold; }
            .receipt-footer { text-align: center; margin-top: 15px; }
            .thank-you { font-size: 12px; font-weight: bold; }
            .powered-by { font-size: 10px; color: #999; margin-top: 10px; }
            @media print { body { padding: 0; } }
          </style>
        </head>
        <body>
          ${receiptRef.value.innerHTML}
        </body>
      </html>
    `)
    printWindow.document.close()
    printWindow.print()
  }
}

onMounted(() => {
  if (props.orderId || props.orderNo) {
    fetchReceipt()
  }
})

// Expose for parent to set receipt data directly
defineExpose({
  setReceipt: (data: Receipt) => { receipt.value = data },
  printReceipt
})
</script>

<style scoped>
.receipt-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.receipt {
  background: white;
  color: #1a1a1a;
  padding: 1.5rem;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  max-width: 300px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.receipt-header {
  text-align: center;
  margin-bottom: 1rem;
}

.logo {
  font-size: 48px;
  margin-bottom: 0.5rem;
}

.store-name {
  font-size: 18px;
  font-weight: bold;
  margin: 0.5rem 0 0.25rem;
}

.store-info {
  font-size: 11px;
  color: #666;
  margin: 2px 0;
}

.divider {
  border-bottom: 1px dashed #ccc;
  margin: 0.75rem 0;
}

.divider.solid {
  border-bottom-style: solid;
  border-color: #333;
}

.divider.thin {
  margin: 0.5rem 0;
}

.order-info, .totals-section, .payment-section {
  margin: 0.5rem 0;
}

.info-row, .total-row, .payment-row {
  display: flex;
  justify-content: space-between;
  margin: 4px 0;
}

.bold {
  font-weight: bold;
}

.items-section {
  margin: 0.5rem 0;
}

.items-header {
  display: flex;
  font-weight: bold;
  font-size: 11px;
  padding-bottom: 4px;
}

.col-item {
  flex: 2;
}

.col-qty, .col-price, .col-total {
  flex: 1;
  text-align: right;
}

.item-row {
  margin: 8px 0;
}

.item-name-row {
  margin-bottom: 2px;
}

.item-name {
  font-weight: 500;
}

.item-variant {
  font-size: 10px;
  color: #666;
  margin-left: 4px;
}

.item-details-row {
  display: flex;
  font-size: 11px;
  color: #444;
}

.item-addons {
  font-size: 10px;
  color: #888;
  margin-left: 8px;
  font-style: italic;
}

.grand-total {
  font-size: 14px;
  font-weight: bold;
  margin-top: 8px;
}

.discount {
  color: #d63031;
}

.change {
  color: #00b894;
  font-weight: bold;
}

.receipt-footer {
  text-align: center;
  margin-top: 1rem;
}

.thank-you {
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.qr-placeholder {
  font-size: 32px;
  margin: 0.5rem 0;
  opacity: 0.3;
}

.powered-by {
  font-size: 10px;
  color: #999;
  margin-top: 0.5rem;
}

.receipt-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
  padding: 1rem;
}

.btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #e2e8f0;
}
</style>
