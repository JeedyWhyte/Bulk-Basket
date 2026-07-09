-- Buyers can only see their own orders
CREATE POLICY "Buyers see own orders"
ON orders FOR SELECT
USING (auth.uid() = buyer_id);

-- Sellers see orders placed with them
CREATE POLICY "Sellers see their orders"
ON orders FOR SELECT
USING (auth.uid() = seller_id);

-- Riders see orders assigned to them
CREATE POLICY "Riders see assigned orders"
ON deliveries FOR SELECT
USING (auth.uid() = rider_id);