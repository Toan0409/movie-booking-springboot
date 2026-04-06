# VNPAY Signature Fix - Progress Tracker

## ✅ PHASE 1: DIAGNOSE (Hoàn thành)
- [x] search_files → Identify VNPAY files  
- [x] read_file: VNPayUtil.java, PaymentServiceImpl.java, VNPayConfig.java, application.properties, PaymentController.java
- [x] Analysis: Code đúng 100%, issue ở config/returnUrl/extra-params
- [x] Plan approved by user

## ✅ PHASE 2.1 COMPLETE: PaymentServiceImpl.java
- [x] 2.1 Add `isValidVNPayParams()` → Reject non-vnp_* params (fix extra params)
- [x] Detailed params logging → DEBUG all callback params  
- [x] Error code 98 "Invalid Params"

## 🔄 PHASE 2: QUICK FIXES (Tiếp tục)
- [ ] 2.2 VNPayConfig.java: @PostConstruct validation (TmnCode/HashSecret not null/empty)
- [ ] 2.3 PaymentController.java: Enhanced logging + param size check

## ⏳ PHASE 3: PRODUCTION READY  
- [ ] 3.1 VNPayUtil: Add vnp_SecureHashType="HmacSHA512"
- [ ] 3.2 IPN IP whitelist (optional)

## 🧪 PHASE 4: TESTING
- [ ] **TEST BÂY GIỜ**: `mvn spring-boot:run` → create payment → check DEBUG logs
- [ ] Share logs HashData create vs callback để confirm fix

## ⏳ PHASE 3: PRODUCTION READY
- [ ] 3.1 VNPayUtil.java: Add vnp_SecureHashType="HmacSHA512" explicit
- [ ] 3.2 Add IPN IP whitelist (optional)

## 🧪 PHASE 4: TESTING
- [ ] 4.1 Local test: mvn spring-boot:run → create payment → manual callback verify
- [ ] 4.2 Share DEBUG logs với user để confirm fix

**Next step:** Implement 2.1 → Update TODO.md after each completion
