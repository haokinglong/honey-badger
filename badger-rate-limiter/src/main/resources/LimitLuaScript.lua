-- 下标从 1 开始 获取key
local key = KEYS[1]
-- 下标从 1 开始 获取参数
local currentTime = tonumber(ARGV[1]) -- 当前时间错
local ttl = tonumber(ARGV[2]) -- 有效
local windowTime = tonumber(ARGV[3]) --
local limitCount = tonumber(ARGV[4])

-- 清除过期的数据
-- 移除指定分数区间内的所有元素，expired 即已经过期的 score
-- 根据当前时间毫秒数 - 超时毫秒数，得到过期时间 expired
redis.call('zremrangebyscore', key, 0, currentTime - windowTime)

-- 获取 zset 中的当前元素个数
local currentNum = tonumber(redis.call('zcard', key))
local next = currentNum + 1

if next > limitCount then
  -- 达到限流大小 返回 0
  return 0;
else
  -- 往 zset 中添加一个值、得分均为当前时间戳的元素，[value,score]
  redis.call("zadd", key, currentTime, currentTime)
  -- 每次访问均重新设置 zset 的过期时间，单位毫秒
  redis.call("expire", key, ttl)
  return next
end