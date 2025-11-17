# Unit Test Results & Integration Testing Plan

## 1. Unit Test Results

### Test Framework Setup
- **JUnit 5**: Version 5.8.2 configured in `pom.xml`.
- **Mockito**: Version 4.11.0 added for dependency mocking.
- **Test File**: `src/test/java/bookshop/BillingServiceTest.java`.

### Current Test Status
- **Total Tests**: 5 test methods defined.
- **Passing Tests**: 5 (all fully implemented).
- **Incomplete Tests**: 0.
- **Build Status**: ✅ All tests pass (`mvn test` succeeds).

### Detailed Results
| Test Method | Status | Notes |
|-------------|--------|-------|
| `testCalculateTotal_RegularCustomer_NoDiscount` | ✅ Pass | Validates 4 units at Rs.100 = Rs.400 for regular customer |
| `testCalculateTotal_RegularCustomer_WithDiscount` | ✅ Pass | Validates 5 units at Rs.95 = Rs.475 for regular customer |
| `testCalculateTotal_VIPCustomer_NoDiscount` | ✅ Pass | Validates 4 units at Rs.100 * 0.95 = Rs.380 for VIP customer |
| `testCalculateTotal_VIPCustomer_WithDiscount` | ✅ Pass | Validates 5 units at Rs.95 * 0.95 = Rs.451.25 for VIP customer |
| `testCalculateTotal_ProductNotFound` | ✅ Pass | Validates exception thrown for unknown product |

### Fixes Applied
- **Module Access**: Added JVM args `--add-opens java.base/java.lang=ALL-UNNAMED` to `maven-surefire-plugin`.
- **Mock Injection**: Used `@Mock(lenient = true)` and manual instantiation of `BillingService`.
- **Exception Handling**: Updated `ProductService.findProductByName()` to throw `InvalidProductException`.
- **Test Setup**: Made fields public and added `throws` to `@BeforeEach`.

### Implemented Logic Validation
- **BillingService**: Core `calculateTotal()` method implemented.
- **Manual Testing**: Logic verified (e.g., 4 units at Rs.100 = Rs.400 for regular customer).
- **Dependencies**: `ProductService` and `CustomerService` load CSV data correctly.

## 2. Integration Testing Plan

### Goals
- Test end-to-end functionality with real dependencies (not mocks).
- Validate data flow from CSV → Services → Billing calculations.
- Ensure UI integration (when implemented).

### Test Scenarios
1. **Data Loading Integration**:
   - Load `products.csv` and `customers.csv`.
   - Verify products/customers are parsed and stored correctly.

2. **Billing Calculation Integration**:
   - Use real `ProductService` and `CustomerService` instances.
   - Test various combinations: regular/VIP customers, different quantities/discounts.

3. **Error Handling Integration**:
   - Test invalid product/customer IDs.
   - Verify exceptions are thrown appropriately.

4. **UI Integration** (Future):
   - Test JavaFX UI with real services.
   - Validate input validation and display updates.

### Implementation Steps
1. **Create Integration Test Class**: `src/test/java/bookshop/IntegrationTest.java`.
2. **Use Real Services**: Instantiate actual `ProductService`, `CustomerService`, `BillingService`.
3. **Test Data**: Use existing CSV files or create test CSVs.
4. **Assertions**: Verify calculations match expected results.
5. **Run with Maven**: `mvn verify` or `mvn integration-test`.

### Timeline
- **Phase 1**: Fix unit test Mockito issues (try JVM args: `--add-opens java.base/java.lang=ALL-UNNAMED`).
- **Phase 2**: Implement integration tests.
- **Phase 3**: Add UI tests with TestFX.

### Expected Outcomes
- Confirm CSV parsing and service interactions work.
- Validate business logic in realistic scenarios.
- Identify integration bugs early.
