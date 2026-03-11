import { test, expect } from '@playwright/test';

test('대기열 진입부터 결제 완료까지의 전체 예약 시나리오를 검증한다', async ({ page }) => {
  // 1. 메인 페이지 로드 및 실시간 인기 공연 클릭
  await page.goto('/');
  const firstConcert = page.locator('section').filter({ hasText: '실시간 인기 공연' }).locator('.snap-center').first();
  await firstConcert.waitFor({ state: 'visible' });
  await firstConcert.click();
  
  // 2. 콘서트 상세 페이지: 대기열 진입
  const enterQueueButton = page.getByRole('button', { name: '예매하기 (대기열 입장)' });
  await enterQueueButton.waitFor({ state: 'visible' });
  await enterQueueButton.click();

  // 3. 대기열 대기 후 좌석 선택 페이지 리다이렉션 대기
  // (Queue Poll 후 /seats로 이동할 때까지 최대 10초 대기)
  await page.waitForURL('**/seats**', { timeout: 10000 });

  // 4. 좌석 선택 페이지
  // .grid 내부의 비활성화(disabled)되지 않은 첫 번째 좌석 버튼 클릭
  const availableSeat = page.locator('.grid button:not([disabled])').first();
  await availableSeat.waitFor({ state: 'visible' });
  await availableSeat.click();
  
  // 선택 완료 후 '결제하기' 버튼 클릭
  const bookButton = page.getByRole('button', { name: '결제하기' });
  await bookButton.click();

  // 5. 결제 화면 진입 및 결제 진행
  await page.waitForURL('**/payment**', { timeout: 10000 });
  // 'NNN원 결제하기' 버튼 클릭
  const payButton = page.getByRole('button', { name: '결제하기' });
  await payButton.click();

  // 6. 완료 페이지 확인 (완료 페이지 문구 '예매가 완료되었습니다')
  await page.waitForURL('**/complete**', { timeout: 10000 });
  await expect(page.getByText('예매가 완료되었습니다')).toBeVisible({ timeout: 10000 });
});
